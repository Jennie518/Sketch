package com.example.lab2

import android.app.Application

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomViewModel(application: Application) : AndroidViewModel(application){

    private val drawingDao: DrawingDao = DrawingDatabase.getDatabase(application).drawingDao()

    // Change from livedata to stateflow
    // StateFlow to hold the drawing path
    private val _drawingPath = MutableStateFlow<Path?>(null)
    private val _colorPaint = MutableStateFlow<Int>(Color.Black.toArgb())
    val drawingPath:  StateFlow<Path?> = _drawingPath
    val colorP : StateFlow<Int> = _colorPaint
    val brushSize = MutableStateFlow(10f)

    //StateFlow to hold the paint brush data
    private val _brushShape = MutableStateFlow<Paint.Cap>(Paint.Cap.ROUND)
    val brushShape: StateFlow<Paint.Cap> get() = _brushShape

    // Save the drawing to StateFlow
    fun saveDrawing(path: Path) {
        _drawingPath.value = path
    }

    fun saveColor(color: Int) {
        _colorPaint.value = color
    }

    fun saveBrushSize(size: Float) {
        brushSize.value = size
    }

    // Update brush shape
    fun setBrushShape(shape: Paint.Cap) {
        _brushShape.value = shape
    }
    // Restore the previous drawing (if any)
    fun getSavedDrawing(): StateFlow<Path?> {
        return drawingPath
    }

    fun getSavedColor(): StateFlow<Int> {
        return colorP
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