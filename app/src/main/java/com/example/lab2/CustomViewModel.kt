package com.example.lab2

import android.app.Application

import android.content.Context
import android.graphics.Bitmap

import android.graphics.Paint

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb

import android.util.Log

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lab2.data.DrawingDao
import com.example.lab2.data.DrawingData
import com.example.lab2.data.DrawingDatabase
import com.example.lab2.data.DrawingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CustomViewModel(application: Application) : AndroidViewModel(application){

    private val drawingDao: DrawingDao = DrawingDatabase.getDatabase(application).drawingDao()
    private val repository: DrawingRepository

    init {
        val drawingDao = DrawingDatabase.getDatabase(application).drawingDao()
        repository = DrawingRepository(drawingDao)
    }



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



    fun saveDrawingToDatabase(context: Context, bitmap: Bitmap, color: Int, brushSize: Float): Boolean {
        val currentTime = System.currentTimeMillis()
        val fileName = "drawing_${currentTime}.png"
        var isSaved = false
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = saveBitmapToStorage(context, bitmap, fileName)
            if (filePath != null) {
                val drawing = DrawingData(
                    filePath = filePath,
                    color = color,
                    brushSize = brushSize,
                    date = currentTime
                )
                repository.insert(drawing)
                isSaved = true
            }
        }
        return isSaved
    }


    fun saveBitmapToStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        val directory = context.getExternalFilesDir("Drawings") ?: return null
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)
        var outputStream: FileOutputStream? = null

        return try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()

            Log.d("SavePath", "Bitmap saved at: ${file.absolutePath}")

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            outputStream?.close()
        }
    }




    fun loadDrawingFromDatabase(drawingId: Int): StateFlow<DrawingData?> {
        val drawingDataFlow = MutableStateFlow<DrawingData?>(null)
        viewModelScope.launch(Dispatchers.IO) {
            repository.getDrawing(drawingId).collect { drawingData ->
                drawingDataFlow.value = drawingData
            }
        }
        return drawingDataFlow
    }

    fun getLastSavedDrawingId(): Flow<Int?> {
        return repository.getLastSavedDrawingId()
    }
    fun getAllDrawings(): LiveData<List<DrawingData>> {
        return drawingDao.getAllDrawings()
    }








}