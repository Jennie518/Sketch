package com.example.lab2


import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

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

    fun getAllDrawings(): LiveData<List<DrawingData>> {
        return drawingDao.getAllDrawings()
    }
}
