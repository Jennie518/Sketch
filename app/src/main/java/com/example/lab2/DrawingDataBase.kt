package com.example.lab2

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(entities = [DrawingData::class], version = 1, exportSchema = false)
abstract class DrawingDatabase : RoomDatabase() {
    abstract fun drawingDao(): DrawingDao

    companion object {
        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        fun getDatabase(context: Context): DrawingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrawingDatabase::class.java,
                    "drawing_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface DrawingDao {
    @Insert
    suspend fun insertDrawing(drawing: DrawingData):Long

    @Insert
    suspend fun insertDrawings(drawings: List<DrawingData>): List<Long>

    @Update
    suspend fun updateDrawing(drawing: DrawingData)

    @Query("SELECT * FROM drawings WHERE id = :id")
    fun getDrawing(id: Int): LiveData<DrawingData>

    @Query("SELECT * FROM drawings")
    fun getAllDrawings(): LiveData<List<DrawingData>>
}