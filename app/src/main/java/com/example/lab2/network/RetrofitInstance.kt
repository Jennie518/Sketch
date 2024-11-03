//package com.example.lab2.network
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitInstance {
//    val api: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl("http://10.0.2.2:8081/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
//}
//
//data class UploadResponse(
//    val message: String,
//    val drawingId: Int
//)