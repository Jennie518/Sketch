package com.example.lab2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CustomView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val path = Path()
    private val paint = Paint()
    private var brushShape = Paint.Cap.ROUND
    private var currColor =  Color.BLACK
    private var brushSize = 10f

    init {
        paint.isAntiAlias = true
        paint.color = currColor
        paint.style = Paint.Style.STROKE
        paint.strokeCap = brushShape
        paint.strokeWidth = brushSize
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(event.x, event.y)
            MotionEvent.ACTION_MOVE -> path.lineTo(event.x, event.y)
        }
        invalidate() // Redraw the view
        return true
    }

    // Set the brush shape (cap style)
    fun setBrushShape(brushShape: BrushShape) {
        paint.strokeCap = when (brushShape) {
            BrushShape.ROUND -> Paint.Cap.ROUND
            BrushShape.SQUARE -> Paint.Cap.SQUARE
        }
//        invalidate() // Redraw with the updated brush shape
    }
//    Set Brush size
    fun setBrushSize(brushSize: Float) {
        paint.strokeWidth = brushSize
        invalidate() // redraw
    }
    fun getBrushSize(): Float {
        return brushSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
        paint.color = currColor
    }

    // Expose methods to interact with the path from the ViewModel
    fun getCurrentPath(): Path {
        return Path(path) // Return a copy of the current path
    }

    fun setPath(newPath: Path) {
        path.set(newPath)
        invalidate() // Redraw the view with the new path
    }

    fun setColor(color: Int){
        currColor = color
    }

    fun getCurrentColor(): Int {
        return currColor
    }


}