package com.sateeshjh.workmanagersample

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface FileApi {

//    @GET("/wp-content/uploads/2022/02/220849-scaled.jpg")
    @GET("/photo-1699960586115-254faf72f378.jpg")
    suspend fun downloadImage(): Response<ResponseBody>

    companion object {

        val instance by lazy {
            Retrofit.Builder()
//                .baseUrl("https://pl-coding.com")
                .baseUrl("https://images.unsplash.com")
                .build()
                .create(FileApi::class.java)
        }
    }
}