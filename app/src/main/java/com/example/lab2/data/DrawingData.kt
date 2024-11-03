package com.example.lab2.data

import com.google.firebase.firestore.PropertyName

data class DrawingData(
    val id: String = "",
    val filePath: String = "",
    val color: Int = 0,
    val brushSize: Float = 0f,
    val date: Long = 0L,
    @PropertyName("shared") val isShared: Boolean = false,
//    val serverDrawingId: Int? = -1
)



