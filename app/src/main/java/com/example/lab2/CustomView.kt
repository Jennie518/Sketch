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
//    private val path = Path()
//    private val paint = Paint()
//    private var brushShape = Paint.Cap.ROUND
//    private var currColor =  Color.BLACK
//    private var brushSize = 10f
    private val paths = mutableListOf<Pair<Path, Paint>>()

    private var currentPath = Path()
    private var currentPaint = Paint()

    init {
//        createNewPaint()
        currentPaint.isAntiAlias = true
        currentPaint.color = Color.BLACK
        currentPaint.strokeWidth = 10f
        currentPaint.style = Paint.Style.STROKE
        currentPaint.strokeCap = Paint.Cap.ROUND
    }
//    private fun setupPaint() {
//
//        currentPaint.isAntiAlias = true
//        currentPaint.color = Color.BLACK
//        currentPaint.strokeWidth = 10f
//        currentPaint.style = Paint.Style.STROKE
//        currentPaint.strokeCap = Paint.Cap.ROUND
//    }
    // Create a new paint object for each new path
    private fun createNewPaint(): Paint {
        val newPaint = Paint()
        newPaint.isAntiAlias = true
        newPaint.color = currentPaint.color
        newPaint.strokeWidth = currentPaint.strokeWidth
        newPaint.style = Paint.Style.STROKE
        newPaint.strokeCap = currentPaint.strokeCap
        return newPaint
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 开始一个新的路径，存储当前的路径和画笔
                currentPath = Path()
                currentPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                // 随手指移动延伸路径
                currentPath.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // 当手指抬起时，将路径和画笔加入列表
                paths.add(Pair(currentPath, currentPaint))
                invalidate()
            }
        }
        return true
    }



    // Set the brush shape (cap style)
    fun setBrushShape(brushShape: BrushShape) {
        currentPaint = createNewPaint()
        currentPaint.strokeCap = when (brushShape) {
            BrushShape.ROUND -> Paint.Cap.ROUND
            BrushShape.SQUARE -> Paint.Cap.SQUARE
        }
//        invalidate() // Redraw with the updated brush shape
    }
//    Set Brush size
    fun setBrushSize(brushSize: Float) {
        currentPaint = createNewPaint()
        currentPaint.strokeWidth = brushSize
    }
    fun getBrushSize(): Float {
        return currentPaint.strokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw all paths with their respective paints
        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }
        // Draw the current path with the current paint
        canvas.drawPath(currentPath, currentPaint)
    }
    // Expose methods to interact with the path from the ViewModel
    fun getCurrentPath(): Path {
        return Path(currentPath) // Return a copy of the current path
    }

    fun setPath(newPath: Path) {
        currentPath.set(newPath)
        invalidate() // Redraw the view with the new path
    }

    fun setColor(color: Int){
        currentPaint = createNewPaint()
        currentPaint.color = color
    }

    fun getCurrentColor(): Int {
        return currentPaint.color
    }

}