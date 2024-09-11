package com.example.lab2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.graphics.Path

class CustomViewModel : ViewModel() {
    // LiveData to hold the drawing path
    private val _drawingPath = MutableLiveData<Path>()
    val drawingPath = _drawingPath as LiveData<Path>

    // Save the drawing to LiveData
    fun saveDrawing(path: Path) {
        _drawingPath.value = path
    }

    // Restore the previous drawing (if any)
    fun getSavedDrawing(): LiveData<Path> {
        return drawingPath
    }
}