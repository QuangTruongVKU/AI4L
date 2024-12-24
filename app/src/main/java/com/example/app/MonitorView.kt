package com.example.app

import MJPEGStreamDecoder
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors

class MonitorView : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var detectionModel: Interpreter
    private lateinit var classificationModel: Interpreter
    private val client = OkHttpClient()
    private var isStreaming = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor_view)

        val monitorIp = intent.getStringExtra("monitorIp") ?: ""
        val monitorPort = intent.getStringExtra("monitorPort")?.toInt() ?: 8080

        initModels()

        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                val esurface = Surface(surface)
                startVideoStream(monitorIp, monitorPort, esurface)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                isStreaming = false
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    private fun startVideoStream(ipAddress: String, port: Int, surfaceTexture: Surface) {
        val url = "http://$ipAddress:$port/video" // Endpoint for continuous video stream (MJPEG, RTSP, etc.)
        Executors.newSingleThreadExecutor().execute {
            try {
                val request = Request.Builder().url(url).build()
                val response: Response = client.newCall(request).execute()

                // Get the response input stream for the video feed
                val inputStream: InputStream = response.body?.byteStream() ?: throw IOException("Empty stream")

                // Start reading frames from the video stream
                val decoder = MJPEGStreamDecoder(inputStream)
                decoder.start()

                while (isStreaming) {
                    val frameBitmap = decoder.getNextFrame()
                    if (frameBitmap != null) {
                        processFrame(frameBitmap)
                    } else {
                        Log.e("MonitorView", "Failed to decode frame: Invalid image data.")
                    }
                }
            } catch (e: IOException) {
                Log.e("MonitorView", "Error in video stream: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun processFrame(bitmap: Bitmap) {
        // Draw the frame on TextureView
        val canvas = textureView.lockCanvas()
        if (canvas != null) {
            try {
                canvas.drawBitmap(bitmap, null, Rect(0, 0, textureView.width, textureView.height), null)
            } catch (e: Exception) {
                Log.e("MonitorView", "Error drawing frame: ${e.message}")
            } finally {
                textureView.unlockCanvasAndPost(canvas)
            }
        }

        // Detect and classify objects in the frame
        detectAndClassify(bitmap)
    }

    private fun detectAndClassify(bitmap: Bitmap) {
        val detectionInput = preprocessImage(bitmap)
        val detectionOutput = Array(1) { Array(8) { FloatArray(1344) } }
        detectionModel.run(detectionInput, detectionOutput)

        val detectedBoxes = mutableListOf<Pair<RectF, Float>>()
        for (j in 0 until 8) {
            for (i in 0 until 1344 step 8) {
                val x = detectionOutput[0][j][i]
                val y = detectionOutput[0][j][i + 1]
                val w = detectionOutput[0][j][i + 2]
                val h = detectionOutput[0][j][i + 3]
                val confidence = detectionOutput[0][j][i + 4]
                if (confidence > 0.5) {
                    val rect = RectF(x, y, x + w, y + h)
                    detectedBoxes.add(Pair(rect, confidence))
                }
            }
        }

        // Classify the detected regions
        for ((rect, confidence) in detectedBoxes) {
            val croppedBitmap = cropRegion(bitmap, rect)
            val classLabel = classifyRegion(croppedBitmap)
            Log.d("Classification", "Class: $classLabel, Confidence: $confidence, Box: $rect")
        }
    }

    private fun classifyRegion(bitmap: Bitmap): Int {
        val classificationInput = preprocessImage(bitmap)
        val classificationOutput = Array(1) { FloatArray(4) }
        classificationModel.run(classificationInput, classificationOutput)
        return classificationOutput[0].indices.maxByOrNull { classificationOutput[0][it] } ?: -1
    }

    private fun cropRegion(bitmap: Bitmap, rect: RectF): Bitmap {
        val left = rect.left.toInt().coerceAtLeast(0)
        val top = rect.top.toInt().coerceAtLeast(0)
        val right = rect.right.toInt().coerceAtMost(bitmap.width)
        val bottom = rect.bottom.toInt().coerceAtMost(bitmap.height)
        return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
    }

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

    private fun initModels() {
        val detectionModelFile = FileUtil.loadMappedFile(this, "best_float32.tflite")
        detectionModel = Interpreter(detectionModelFile, Interpreter.Options().apply { setNumThreads(4) })

        val classificationModelFile = FileUtil.loadMappedFile(this, "model_float32.tflite")
        classificationModel = Interpreter(classificationModelFile, Interpreter.Options().apply { setNumThreads(4) })
    }

    override fun onDestroy() {
        super.onDestroy()
        isStreaming = false
    }
}
