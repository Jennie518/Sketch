package com.example.lab2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.graphics.Path
import android.graphics.Paint

class CustomViewModel : ViewModel() {
    // LiveData to hold the drawing path
    private val _drawingPath = MutableLiveData<Path>()
    private val _colorPaint = MutableLiveData<Int>()
    val drawingPath = _drawingPath as LiveData<Path>
    val colorP = _colorPaint as LiveData<Int>

    //LiveData to hold the paint brush data
    private val _brushShape = MutableLiveData<Paint.Cap>().apply { value = Paint.Cap.ROUND } // Default shape
    val brushShape: LiveData<Paint.Cap> get() = _brushShape

    // Save the drawing to LiveData
    fun saveDrawing(path: Path) {
        _drawingPath.value = path
    }

    fun saveColor(color: Int) {
        _colorPaint.value = color
    }

    // Restore the previous drawing (if any)
    fun getSavedDrawing(): LiveData<Path> {
        return drawingPath
    }

    fun getSavedColor(): LiveData<Int> {
        return colorP
    }

    // Update brush shape
    fun setBrushShape(shape: Paint.Cap) {
        _brushShape.value = shape
    }
}