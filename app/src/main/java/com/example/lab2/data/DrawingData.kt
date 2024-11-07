package com.example.lab2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawings")
data class DrawingData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val color: Int,
    val brushSize: Float,
    val date: Long,
    val isShared: Boolean = false,
    val serverDrawingId: Int? = null
)

