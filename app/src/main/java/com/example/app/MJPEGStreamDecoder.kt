package com.example.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.concurrent.LinkedBlockingQueue

class MJPEGStreamDecoder(private val inputStream: InputStream) {

    private val frameQueue: LinkedBlockingQueue<Bitmap> = LinkedBlockingQueue()

    // This method will start reading frames from the MJPEG stream
    fun start() {
        Thread {
            var frameData = ByteArray(0)
            val byteArrayOutputStream = java.io.ByteArrayOutputStream()

            try {
                while (true) {
                    val byteRead = inputStream.read()
                    if (byteRead == -1) {
                        break
                    }

                    byteArrayOutputStream.write(byteRead)

                    // Check if the last 4 bytes are the end-of-frame marker "\r\n\r\n"
                    val byteArray = byteArrayOutputStream.toByteArray()
                    if (byteArray.size > 4 &&
                        byteArray[byteArray.size - 4] == 0xFF.toByte() &&
                        byteArray[byteArray.size - 3] == 0xD9.toByte() &&
                        byteArray[byteArray.size - 2] == 0xFF.toByte() &&
                        byteArray[byteArray.size - 1] == 0xD9.toByte()) {
                        frameData = byteArray
                        byteArrayOutputStream.reset()
                        val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(frameData))
                        if (bitmap != null) {
                            frameQueue.put(bitmap)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    // Get the next frame from the stream
    fun getNextFrame(): Bitmap? {
        return try {
            frameQueue.take()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
    }
}
