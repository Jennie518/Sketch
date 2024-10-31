package com.example.lab2

import android.app.Application
import android.content.ContentValues

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb

import android.util.Log
import androidx.core.content.FileProvider

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.lab2.data.DrawingDao
import com.example.lab2.data.DrawingData
import com.example.lab2.data.DrawingDatabase
import com.example.lab2.data.DrawingRepository
import com.example.lab2.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class CustomViewModel(application: Application) : AndroidViewModel(application){
    private val _importedBitmap = MutableStateFlow<Bitmap?>(null)
    val importedBitmap: StateFlow<Bitmap?> = _importedBitmap

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


    fun saveDrawingToDatabase(
        context: Context,
        bitmap: Bitmap,
        color: Int,
        brushSize: Float,
        onComplete: (Boolean) -> Unit
    ) {
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
                Log.d("SaveDrawing", "Drawing saved successfully in the database")
                withContext(Dispatchers.Main) {
                    onComplete(true)
                }
            } else {
                Log.e("SaveDrawing", "Failed to save the bitmap to storage")
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }



    //    save drawing to device storage
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
            Log.e("SavePath", "Failed to save bitmap: ${e.localizedMessage}")
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

    fun shareDrawing(context: Context, bitmap: Bitmap) {
        val fileName = "drawing_${System.currentTimeMillis()}.png"
        val filePath = saveBitmapToStorage(context, bitmap, fileName)

        filePath?.let {
            val file = File(it)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share drawing via"))
        }
    }


    fun saveImportedBitmap(bitmap: Bitmap) {
        _importedBitmap.value = bitmap
    }
    fun importImage(context: Context, uri: android.net.Uri, onSuccess: (Bitmap) -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val importedBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (importedBitmap != null) {
                    withContext(Dispatchers.Main) {
                        onSuccess(importedBitmap)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onFailure()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
        }
    }


    fun getLastSavedDrawingId(): Flow<Int?> {
        return repository.getLastSavedDrawingId()
    }
    fun getAllDrawings(): LiveData<List<DrawingData>> {
        return drawingDao.getAllDrawings()
    }

    fun uploadDrawingToServer(
        file: File,
        color: Int,
        brushSize: Float,
        userId: String,
        drawingId: Int,
        onSuccess: (Int) -> Unit // 添加一个回调函数，用于上传成功后处理
    ) {
        val fileReqBody = file.asRequestBody("image/png".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, fileReqBody)


        val colorReqBody = color.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val brushSizeReqBody = brushSize.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val userIdReqBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.uploadDrawing(filePart, colorReqBody, brushSizeReqBody, userIdReqBody)
                if (response.isSuccessful) {
                    Log.d("UploadDrawing", "File uploaded successfully")

                    val serverDrawingId = response.body()?.drawingId ?: -1

                    if (serverDrawingId != -1) {
                        onSuccess(serverDrawingId) // 调用成功回调函数，将ID传递给它
                    } else {
                        Log.e("UploadDrawing", "Failed to retrieve server drawing ID")
                    }
                } else {
                    Log.e("UploadDrawing", "Failed to upload file. Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("UploadDrawing", "Exception occurred during upload: ${e.localizedMessage}")
            }
        }
    }

    fun updateDrawingSharedStatus(drawingId: Int, isShared: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            drawingDao.updateDrawingSharedStatus(drawingId, isShared)
        }
    }

    fun updateDrawingServerId(drawingId: Int, serverDrawingId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            drawingDao.updateDrawingServerId(drawingId, serverDrawingId)
        }
    }

    fun unshareDrawingFromServer(drawingId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteDrawing(drawingId)
                if (response.isSuccessful) {
                    Log.d("UnshareDrawing", "File unshared successfully")
                    updateDrawingSharedStatus(drawingId, false)
                } else {
                    Log.e("UnshareDrawing", "Failed to unshare file. Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("UnshareDrawing", "Exception occurred during unshare: ${e.localizedMessage}")
            }
        }
    }



    fun saveDrawingToGallery(context: Context, bitmap: Bitmap, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val filename = "drawing_${System.currentTimeMillis()}.png"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyDrawings")
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                var outputStream: OutputStream? = null
                try {
                    outputStream = resolver.openOutputStream(uri)
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        withContext(Dispatchers.Main) {
                            onComplete(true)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            onComplete(false)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        onComplete(false)
                    }
                } finally {
                    outputStream?.close()
                }
            } else {
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }








}