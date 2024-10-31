package com.example.lab2.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiService {
    @Multipart
    @POST("uploadDrawing")
    suspend fun uploadDrawing(
        @Part file: MultipartBody.Part,
        @Part("color") color: RequestBody,
        @Part("brushSize") brushSize: RequestBody,
        @Part("userId") userId: RequestBody
    ): Response<UploadResponse>

    @DELETE("drawings/{id}")
    suspend fun deleteDrawing(
        @Path("id") drawingId: Int
    ): Response<ResponseBody>
}