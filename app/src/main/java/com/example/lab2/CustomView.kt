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
    private var hasDrawn = false
    private val paths = mutableListOf<Pair<Path, Paint>>()
    private var bitmap: Bitmap? = null

    private var currentPath = Path()
    private var currentPaint = Paint()

    // Temporarily saves the properties of the next stroke
    private var nextColor: Int = Color.BLACK
    private var nextBrushSize: Float = 10f
    private var nextBrushShape: Paint.Cap = Paint.Cap.ROUND

    init {
        currentPaint.isAntiAlias = true
        currentPaint.color = Color.BLACK
        currentPaint.strokeWidth = 10f
        currentPaint.style = Paint.Style.STROKE
        currentPaint.strokeCap = Paint.Cap.ROUND
    }

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
                // Apply a new brush property each time a new brush is started
                currentPaint = createNewPaint().apply {
                    color = nextColor
                    strokeWidth = nextBrushSize
                    strokeCap = nextBrushShape
                }
                currentPath = Path()
                currentPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(event.x, event.y)
                hasDrawn = true
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                paths.add(Pair(currentPath, currentPaint))
                invalidate()
            }
        }
        return true
    }
    fun hasDrawnAnything(): Boolean {
        return hasDrawn
    }

    fun setBitmap(bitmap: Bitmap) {
        if (bitmap != null) {
            this.bitmap = bitmap
            Log.d("CustomView", "Bitmap set successfully with size: ${bitmap.width}x${bitmap.height}")
        } else {
            Log.e("CustomView", "Bitmap is null when calling setBitmap")
        }
        invalidate()
    }

    fun setColor(color: Int){
        nextColor = color
    }

    fun setBrushShape(brushShape: BrushShape) {
        nextBrushShape = when (brushShape) {
            BrushShape.ROUND -> Paint.Cap.ROUND
            BrushShape.SQUARE -> Paint.Cap.SQUARE
        }
    }

    fun setBrushSize(brushSize: Float) {
        nextBrushSize = brushSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            Log.d("CustomView", "Drawing bitmap on canvas, size: ${it.width}x${it.height}")
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
            Log.d("CustomView", "Drawing path on canvas")
        }

        canvas.drawPath(currentPath, currentPaint)
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }

        canvas.drawPath(currentPath, currentPaint)

        return bitmap
    }
}