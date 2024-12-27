package com.example.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val detectedBoxes = mutableListOf<Triple<RectF, Float, String>>()
    private val paint = Paint().apply {
        color = android.graphics.Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val textPaint = Paint().apply {
        color = android.graphics.Color.YELLOW
        textSize = 30f
        style = Paint.Style.FILL
    }

    fun updateBoxes(boxes: List<Triple<RectF, Float, String>>) {
        detectedBoxes.clear()
        detectedBoxes.addAll(boxes)
        invalidate() // Refresh view
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (box in detectedBoxes) {
            canvas.drawRect(box.first, paint)
            val yPosition = box.first.top - 10f
            if (yPosition > 0) {
                canvas.drawText(box.third, box.first.left, yPosition, textPaint)
            }
        }
    }

}
