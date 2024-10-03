package com.example.lab2

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.graphics.Path
import android.graphics.Paint
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomViewModel(application: Application) : AndroidViewModel(application){

    private val drawingDao: DrawingDao = DrawingDatabase.getDatabase(application).drawingDao()

    // LiveData to hold the drawing path
    private val _drawingPath = MutableLiveData<Path>()
    private val _colorPaint = MutableLiveData<Int>()
    val drawingPath = _drawingPath as LiveData<Path>
    val colorP = _colorPaint as LiveData<Int>
    val brushSize = MutableLiveData<Float>()

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
    fun saveBrushSize(size: Float) {
        brushSize.value = size
    }
    fun getSavedColor(): LiveData<Int> {
        return colorP
    }

    // Update brush shape
    fun setBrushShape(shape: Paint.Cap) {
        _brushShape.value = shape
    }

    fun saveDrawingToDatabase(path: String, color: Int, brushSize: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val drawing = DrawingData(pathData = path, color = color, brushSize = brushSize)
            val id = drawingDao.insertDrawing(drawing)
        }
    }

    fun loadDrawingFromDatabase(id: Int): LiveData<DrawingData> {
        return drawingDao.getDrawing(id)
    }

}