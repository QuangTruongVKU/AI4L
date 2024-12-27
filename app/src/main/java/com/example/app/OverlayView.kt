    package com.example.app

    import android.content.Context
    import android.graphics.*
    import android.util.AttributeSet
    import android.view.View

    class OverlayView(context: Context, attrs: AttributeSet) : View(context, attrs) {
        private var detectedBoxes: List<Triple<RectF, Float, String>> = listOf()

        // Cập nhật bounding boxes cần vẽ
        fun setDetectedBoxes(boxes: List<Triple<RectF, Float, String>>) {
            detectedBoxes = boxes
            invalidate() // Yêu cầu vẽ lại View
        }

        // Override onDraw để vẽ bounding boxes lên View
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val paint = Paint().apply {
                color = Color.RED
                strokeWidth = 5f
                style = Paint.Style.STROKE
            }
            val textPaint = Paint().apply {
                color = Color.YELLOW
                textSize = 30f
                style = Paint.Style.FILL
            }

            detectedBoxes.forEach { box ->
                val rect = box.first
                // Vẽ bounding box
                canvas.drawRect(rect, paint)
                // Vẽ label bên trên bounding box
                canvas.drawText(box.third, rect.left, rect.top - 10, textPaint)
            }
        }
    }
