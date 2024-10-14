package com.example.lab2

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.graphics.Path
import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CustomViewModel(application: Application) : AndroidViewModel(application){

//    private val drawingDao: DrawingDao = DrawingDatabase.getDatabase(application).drawingDao()
    private val repository: DrawingRepository

    init {
        val drawingDao = DrawingDatabase.getDatabase(application).drawingDao()
        repository = DrawingRepository(drawingDao)
    }



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

    fun saveDrawingToDatabase(context: Context, bitmap: Bitmap, color: Int, brushSize: Float) {
        val currentTime = System.currentTimeMillis()
        val fileName = "drawing_${currentTime}.png"
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
            }
        }
    }

    fun saveBitmapToStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        val directory = context.getExternalFilesDir("Drawings")
        if (directory != null && !directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)
        var outputStream: FileOutputStream? = null

        return try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) // 将 Bitmap 压缩为 PNG 并写入文件
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


    fun loadDrawingFromDatabase(drawingId: Int): LiveData<DrawingData> {
        return repository.getDrawing(drawingId)
    }
    fun getLastSavedDrawingId(): LiveData<Int?> {
        return repository.getLastSavedDrawingId()
    }






}