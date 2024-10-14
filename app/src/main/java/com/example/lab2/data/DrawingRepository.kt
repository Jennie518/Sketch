package com.example.lab2.data


import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class DrawingRepository(private val drawingDao: DrawingDao) {

    suspend fun insert(drawing: DrawingData) {
        drawingDao.insertDrawing(drawing)
    }

    suspend fun insertDrawings(drawings: List<DrawingData>) {
        drawingDao.insertDrawings(drawings)
    }

    suspend fun update(drawing: DrawingData) {
        drawingDao.updateDrawing(drawing)
    }

    fun getLastSavedDrawingId(): Flow<Int?> {
        return drawingDao.getLastDrawingAsFlow()
    }

    fun getDrawing(id: Int): Flow<DrawingData> {
        return drawingDao.getDrawingAsFlow(id)
    }

}
