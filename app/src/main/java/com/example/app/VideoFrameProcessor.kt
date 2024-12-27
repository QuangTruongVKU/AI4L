package com.example.app

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.view.Surface
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.opengles.GL10.GL_RGBA
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE

class VideoFrameProcessor(private val player: ExoPlayer) {

    private var frameListener: ((Bitmap) -> Unit)? = null
    private lateinit var surfaceTexture: SurfaceTexture
    private lateinit var surface: Surface
    private var videoWidth: Int = 0
    private var videoHeight: Int = 0
    private var textureId: Int = 0  // ID cho texture OpenGL

    init {
        // Cấu hình ExoPlayer để lấy frame video
        player.addListener(object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                super.onVideoSizeChanged(videoSize)
                // Lấy kích thước video khi thay đổi
                videoWidth = videoSize.width
                videoHeight = videoSize.height
            }

            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                // Lấy frame đầu tiên sau khi video bắt đầu phát
            }
        })

        // Tạo một OpenGL Texture ID hợp lệ
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        textureId = textureIds[0]

        // Khởi tạo SurfaceTexture với Texture ID hợp lệ
        surfaceTexture = SurfaceTexture(textureId)
        surface = Surface(surfaceTexture)

        player.setVideoSurface(surface)  // Kết nối ExoPlayer với Surface
    }

    // Cấu hình listener để nhận frame
    fun setFrameListener(listener: (Bitmap) -> Unit) {
        this.frameListener = listener
    }

    // Phương thức để lấy Bitmap từ video
    private fun getBitmapFromVideo(): Bitmap? {
        // Kiểm tra xem video có được cập nhật kích thước chưa
        if (videoWidth == 0 || videoHeight == 0) {
            return null
        }

        surfaceTexture.updateTexImage()  // Cập nhật dữ liệu hình ảnh từ SurfaceTexture

        // Tạo một ByteBuffer để lưu trữ dữ liệu hình ảnh
        val buffer = ByteBuffer.allocateDirect(4 * videoWidth * videoHeight * 3)
        buffer.order(ByteOrder.nativeOrder())  // Đảm bảo ByteOrder là native order

        // Kiểm tra OpenGL context trước khi đọc pixel
        if (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
            // Lỗi context OpenGL
            return null
        }

        // Đọc dữ liệu từ SurfaceTexture (cần OpenGL context)
        glReadPixels(0, 0, videoWidth, videoHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer)

        // Tạo Bitmap từ dữ liệu pixel
        buffer.rewind()  // Reset vị trí của buffer
        val pixels = IntArray(videoWidth * videoHeight)
        buffer.asIntBuffer().get(pixels)

        return Bitmap.createBitmap(pixels, videoWidth, videoHeight, Bitmap.Config.ARGB_8888)
    }

    // Phương thức này sẽ gọi frameListener mỗi khi có frame video mới
    fun processFrame() {
        val bitmap = getBitmapFromVideo()
        bitmap?.let {
            frameListener?.invoke(it)
        }
    }

    // Phương thức OpenGL để lấy dữ liệu pixel từ SurfaceTexture
    private fun glReadPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, buffer: ByteBuffer) {
        GLES20.glReadPixels(x, y, width, height, format, type, buffer)

        // Kiểm tra lỗi sau khi gọi glReadPixels
        if (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
            // Lỗi khi đọc pixels
            buffer.clear()  // Dọn dẹp buffer
        }
    }
}
