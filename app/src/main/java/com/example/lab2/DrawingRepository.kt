package com.example.lab2


import androidx.lifecycle.LiveData

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

    fun getLastSavedDrawingId(): LiveData<Int?> {
        return drawingDao.getLastDrawingId()
    }

    fun getDrawing(id: Int): LiveData<DrawingData> {
        return drawingDao.getDrawing(id)
    }

    fun getAllDrawings(): LiveData<List<DrawingData>> {
        return drawingDao.getAllDrawings()
    }
}
