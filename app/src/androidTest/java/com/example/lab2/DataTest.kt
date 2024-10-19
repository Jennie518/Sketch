package com.example.lab2.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DrawingDatabaseInstrumentedTest {

    private lateinit var drawingDao: DrawingDao
    private lateinit var db: DrawingDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DrawingDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        drawingDao = db.drawingDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAndRetrieveDrawing() = runBlocking {
        // Given a DrawingData object
        val drawing = DrawingData(
            filePath = "path/to/drawing.png",
            color = 0xFF000000.toInt(),
            brushSize = 10f,
            date = System.currentTimeMillis()
        )

        // When the drawing is inserted
        val drawingId = drawingDao.insertDrawing(drawing)

        // Retreive drawing by id
        val retrievedDrawing = drawingDao.getDrawingAsFlow(drawingId.toInt()).first()

        assertNotNull(retrievedDrawing)
        assertEquals(drawing.filePath, retrievedDrawing.filePath)
        assertEquals(drawing.color, retrievedDrawing.color)
        assertEquals(drawing.brushSize, retrievedDrawing.brushSize, 0.0f)
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateDrawing() = runBlocking {
        // Given a DrawingData object
        val drawing = DrawingData(
            filePath = "path/to/drawing.png",
            color = 0xFF000000.toInt(),
            brushSize = 10f,
            date = System.currentTimeMillis()
        )

        // When the drawing is inserted
        val drawingId = drawingDao.insertDrawing(drawing)

        // Update the drawing's brush size and color
        val updatedDrawing = drawing.copy(id = drawingId.toInt(), color = 0xFFFF0000.toInt(), brushSize = 20f)
        drawingDao.updateDrawing(updatedDrawing)

        // Retrieve the updated drawing
        val retrievedDrawing = drawingDao.getDrawingAsFlow(drawingId.toInt()).first()

        assertEquals(0xFFFF0000.toInt(), retrievedDrawing.color)
        assertEquals(20f, retrievedDrawing.brushSize, 0.0f)
    }
}
