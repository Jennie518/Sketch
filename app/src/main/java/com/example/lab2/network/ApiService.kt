package com.example.lab2.network

import com.example.lab2.data.DrawingData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
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
        @Path("id") drawingId: Int,
        @Header("userId") userId: String
    ): Response<ResponseBody>

    @GET("/drawings/{id}")
    suspend fun getDrawingFileById(@Path("id") drawingId: Int): Response<ResponseBody>

}