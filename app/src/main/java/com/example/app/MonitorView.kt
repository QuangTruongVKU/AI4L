package com.example.app

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MonitorView : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var detectionModel: Interpreter
    private lateinit var classificationModel: Interpreter
    private lateinit var surfaceView: SurfaceView
    private var isStreaming = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_view)

        val btnExit = findViewById<ImageButton>(R.id.btnExit)
        btnExit.setOnClickListener { finish() }

        val monitorIp = intent.getStringExtra("monitorIp") ?: ""
        val monitorPort = intent.getStringExtra("monitorPort") ?: "554"
        val rtspUrl = "rtsp://$monitorIp:$monitorPort/h264_ulaw.sdp"

        initModels()

        surfaceView = findViewById(R.id.surfaceView)
        val surfaceHolder = surfaceView.holder

        val player = ExoPlayer.Builder(this).build()
        playerView = findViewById(R.id.playView)
        playerView.player = player

        val mediaSource: MediaSource = RtspMediaSource.Factory()
            .createMediaSource(MediaItem.fromUri(rtspUrl))

        player.setMediaSource(mediaSource)
        player.prepare()
        player.play()

        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                player.setVideoSurface(holder.surface)
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                player.setVideoSurface(null)
            }
        })

        val videoFrameProcessor = VideoFrameProcessor(player)
        videoFrameProcessor.setFrameListener { bitmap ->
            processFrame(bitmap)
        }
    }

    private fun processFrame(bitmap: Bitmap) {
        val detectedBoxes = detectObjects(bitmap)
        classifyObjects(detectedBoxes.toMutableList(), bitmap)

        val scaledBoxes = scaleBoxes(detectedBoxes, bitmap)

        // Cập nhật bounding boxes trong OverlayView
        runOnUiThread {
            val overlayView = findViewById<OverlayView>(R.id.overlayView)
            overlayView.setDetectedBoxes(scaledBoxes)
        }
    }


    private fun scaleBoxes(
        boxes: List<Triple<RectF, Float, String>>,
        bitmap: Bitmap
    ): List<Triple<RectF, Float, String>> {
        // Ensure the scale is correct
        val scaleX = surfaceView.width / bitmap.width.toFloat()
        val scaleY = surfaceView.height / bitmap.height.toFloat()

        return boxes.map { box ->
            // Print the original and scaled coordinates for debugging
            println( "Original box: ${box.first}")

            val scaledRect = RectF(
                box.first.left * scaleX,
                box.first.top * scaleY,
                box.first.right * scaleX,
                box.first.bottom * scaleY
            )

            println( "Scaled box: $scaledRect")

            Triple(scaledRect, box.second, box.third)
        }
    }

    //    // Update the canvas drawing to use overlayView
    //        private fun updateFrame(frame: Bitmap, detectedBoxes: List<Triple<RectF, Float, String>>) {
    //        // Scale bounding boxes if necessary
    //        val scaledBoxes = scaleBoxes(detectedBoxes, frame)
    //
    //        // Update OverlayView with the current bounding boxes
    //        runOnUiThread {
    //            val overlayView = findViewById<OverlayView>(R.id.overlayView)
    //            overlayView.setDetectedBoxes(scaledBoxes)
    //        }
    //
    //        // Optionally, update any other UI components like the player view
    //        val playerView = findViewById<PlayerView>(R.id.playView)
    //        playerView.setAspectRatio(frame.width.toFloat() / frame.height.toFloat())
    //    }
    //
    //
    //    private fun drawOverlay(
    //        canvas: Canvas,
    //        detectedBoxes: List<Triple<RectF, Float, String>>
    //    ) {
    //        val paint = Paint().apply {
    //            color = Color.RED
    //            strokeWidth = 5f
    //            style = Paint.Style.STROKE
    //        }
    //        val textPaint = Paint().apply {
    //            color = Color.YELLOW
    //            textSize = 30f
    //            style = Paint.Style.FILL
    //        }
    //
    //        detectedBoxes.forEach { box ->
    //            val rect = box.first
    //            // Log the bounding box coordinates to check if they are within the visible canvas
    //            println("Bounding box: Left: ${rect.left}, Top: ${rect.top}, Right: ${rect.right}, Bottom: ${rect.bottom}")
    //
    //            // Draw the bounding box rectangle
    //            canvas.drawRect(rect, paint)
    //            // Draw the label text above the bounding box (class name or label)
    //            canvas.drawText(box.third, rect.left, rect.top - 10, textPaint) // box.third contains the class name
    //        }
    //    }
    //



    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true)
        val buffer = ByteBuffer.allocateDirect(4 * 256 * 256 * 3).apply { order(ByteOrder.nativeOrder()) }

        val pixels = IntArray(256 * 256)
        resizedBitmap.getPixels(pixels, 0, 256, 0, 0, 256, 256)
        for (pixel in pixels) {
            buffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
            buffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
            buffer.putFloat((pixel and 0xFF) / 255.0f)        // Blue
        }
        buffer.rewind()
        return buffer
    }
    private fun detectObjects(bitmap: Bitmap): List<Triple<RectF, Float, String>> {
        val detectionInput = preprocessImage(bitmap)
        val detectionOutput = Array(1) { Array(8) { FloatArray(1344) } }
        detectionModel.run(detectionInput, detectionOutput)

        val detectedBoxes = mutableListOf<Triple<RectF, Float, String>>()
        for (j in 0 until 8) {
            for (i in 0 until 1344 step 8) {
                val x = detectionOutput[0][j][i]
                val y = detectionOutput[0][j][i + 1]
                val w = detectionOutput[0][j][i + 2]
                val h = detectionOutput[0][j][i + 3]
                val confidence = detectionOutput[0][j][i + 4]
                if (confidence > 0.5) {
                    val rect = RectF(x, y, x + w, y + h)
                    detectedBoxes.add(Triple(rect, confidence, "Class"))
                }
            }
        }
        return detectedBoxes
    }
    private fun preprocessImageForClassification(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3).apply { order(ByteOrder.nativeOrder()) }

        val pixels = IntArray(224 * 224)
        resizedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224)
        for (pixel in pixels) {
            buffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
            buffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
            buffer.putFloat((pixel and 0xFF) / 255.0f)        // Blue
        }
        buffer.rewind()
        return buffer
    }

    private fun classifyObjects(detectedBoxes: MutableList<Triple<RectF, Float, String>>, bitmap: Bitmap) {
        detectedBoxes.forEachIndexed { index, box ->
            try {
                val croppedBitmap = cropBitmap(bitmap, box.first)
                val classificationInput = preprocessImageForClassification(croppedBitmap)
                val classificationOutput = Array(1) { FloatArray(10) }
                classificationModel.run(classificationInput, classificationOutput)
                val classIndex = classificationOutput[0].withIndex().maxByOrNull { it.value }?.index ?: -1
                detectedBoxes[index] = box.copy(third = "Class $classIndex") // Updating the label to include class index
            } catch (e: Exception) {
                Log.e("MonitorView", "Error cropping bitmap: ${e.message}")
            }
        }
    }


    private fun cropBitmap(bitmap: Bitmap, rect: RectF): Bitmap {
        val left = rect.left.coerceIn(0f, bitmap.width.toFloat()).toInt()
        val top = rect.top.coerceIn(0f, bitmap.height.toFloat()).toInt()
        val width = rect.width().coerceAtMost(bitmap.width - left.toFloat()).toInt()
        val height = rect.height().coerceAtMost(bitmap.height - top.toFloat()).toInt()
        return Bitmap.createBitmap(bitmap, left, top, width, height)
    }

    private fun initModels() {
        val detectionModelFile = FileUtil.loadMappedFile(this, "best_float32.tflite")
        detectionModel = Interpreter(detectionModelFile, Interpreter.Options().apply { setNumThreads(4) })

        val classificationModelFile = FileUtil.loadMappedFile(this, "model_float32.tflite")
        classificationModel = Interpreter(classificationModelFile, Interpreter.Options().apply { setNumThreads(4) })
    }

    override fun onDestroy() {
        super.onDestroy()
        isStreaming = false
        playerView.player?.release()
        detectionModel.close()
        classificationModel.close()
    }
}
