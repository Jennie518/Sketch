package com.example.lab2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val path = Path()
    private val paint = Paint()
    private var savedPath: Path? = null

    init {
        paint.color = android.graphics.Color.BLACK
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(event.x, event.y)
            MotionEvent.ACTION_MOVE -> path.lineTo(event.x, event.y)
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    // Save the current drawing
    fun saveCurrentDrawing() {
        savedPath = Path(path) //Save the current path
    }

    // Restore the previous drawing
    fun restoreSavedDrawing() {
        if (savedPath != null) {
            path.set(savedPath!!) // Restore the saved path
            invalidate() // Redraw the view
        }
    }
}