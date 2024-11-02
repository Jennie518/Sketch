package com.example.lab2.data


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.contentcapture.ContentCaptureManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.lab2.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream

class DrawingRepository(private val coroutineScope: CoroutineScope) {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun insert(drawing: DrawingData, onComplete: (String) -> Unit) {

        db.collection("drawings")
            .add(drawing)
            .addOnSuccessListener { docmentRefence ->
                val generatedId = docmentRefence.id
                db.collection("drawings").document(generatedId).update("id", generatedId)
                Log.d(
                    "generatedId",
                    "${docmentRefence.id}"
                )
                Log.d(
                    "firestore",
                    "insert a drawing to firestore db successfully"
                )
                onComplete(generatedId)
            }
            .addOnFailureListener { Log.d("firestore", "fail to insert drawing to firestore") }
    }

    suspend fun update(drawing: DrawingData) {
        try {
            db.collection("drawings")
                .document(drawing.id)
                .set(drawing)
                .await()
            Log.e("firestore", "update drawing to firestore db successfully")
        } catch (e: Exception) {
            Log.e("firestore", "fail to update drawing to firestore")
        }
    }

    suspend fun getDrawing(id: String): DrawingData? {
        return try {
            db.collection("drawings")
                .document(id)
                .get()
                .await()
                .toObject(DrawingData::class.java)
        } catch (e: Exception) {
            Log.e("firestore", "Error fetching drawing", e)
            null
        }
    }

    fun getAllDrawings(): LiveData<List<DrawingData>> {
        val drawingLiveData = MutableLiveData<List<DrawingData>>()

        db.collection("drawings")
            .get()
            .addOnSuccessListener { documents ->
                val drawingList = documents.mapNotNull { it.toObject(DrawingData::class.java) }
                drawingLiveData.value = drawingList
            }
            .addOnFailureListener { exception ->
                Log.e("firestore", "failt to get all drawings", exception)
                drawingLiveData.value = emptyList()
            }
        return drawingLiveData
    }

    fun uploadBitmapToStorage(filePath: String, onComplete: (Boolean, String?) -> Unit) {
        val storageRef = storage.reference.child("drawings/${System.currentTimeMillis()}.png")
        val file = Uri.fromFile(File(filePath))

        storageRef.putFile(file)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(true, uri.toString())
                }
            }
            .addOnFailureListener {
                onComplete(false, null)
                Log.e("storage", "Fail to upload file")
            }
    }

    fun saveBitmapToStorage(context: Context, bitmap: Bitmap, fileName: String): String? {
        return try {
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    fun updateDrawingSharedStatus(drawingId: String, isShared: Boolean) {
        db.collection("drawings")
            .document(drawingId)
            .update("isShared", isShared)
            .addOnSuccessListener {
                Log.d(
                    "firestore",
                    "drawing shared status updated successfully"
                )
            }
            .addOnFailureListener { e -> Log.e("firestore", "fail to update shared status") }
    }

    fun updateDrawingServerId(drawingId: String, serverDrawingId: Int) {
        db.collection("drawings")
            .document(drawingId)
            .update("serverDrawingId", serverDrawingId)
            .addOnSuccessListener { Log.e("DrawingServerId", "DrawingServerID saved!") }
            .addOnFailureListener { e ->
                Log.e(
                    "DrawingServerId",
                    "fail update server ID ${e.printStackTrace()}"
                )
            }
    }

    fun unshareDrawingFromServer(drawingId: String, onUnsharedSucess: () -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.deleteDrawing(drawingId)
                if (response.isSuccessful) {
                    Log.d("UnshareDrawing", "File unshared successfully")
                    updateDrawingSharedStatus(drawingId, false)

                    onUnsharedSucess()
                } else {
                    Log.e(
                        "UnshareDrawing",
                        "Failed to unshare file. Error code: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("UnshareDrawing", "Exception occurred during unshare: ${e.localizedMessage}")
            }
        }
    }

    fun loadDrawingFromDatabase(drawingId: String): StateFlow<DrawingData?> {
        val drawingDataFlow = MutableStateFlow<DrawingData?>(null)
        CoroutineScope(Dispatchers.IO).launch {
            val drawingData = getDrawing(drawingId)
            drawingDataFlow.value = drawingData
        }
        return drawingDataFlow
    }

}
