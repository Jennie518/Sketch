package com.example.lab2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CustomView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    //    private val path = Path()
//    private val paint = Paint()
//    private var brushShape = Paint.Cap.ROUND
//    private var currColor =  Color.BLACK
//    private var brushSize = 10f
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var bitmap: Bitmap? = null



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
                // Create new Path and Paint when fingers touches the canvas
                currentPath = Path()
                currentPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                // connect the path to the end of the path
                currentPath.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // add path and paint into the list when fingers leave the canvas
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
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
        // Draw all paths with their respective paints
        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }
        // Draw the current path with the current paint
        canvas.drawPath(currentPath, currentPaint)
    }


    fun setColor(color: Int){
        currentPaint = createNewPaint()
        currentPaint.color = color
    }

    fun getCurrentColor(): Int {
        return currentPaint.color
    }
    fun getBitmap(): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
        Log.d("CustomView", "Bitmap set successfully in CustomView")
        invalidate()

    }


}