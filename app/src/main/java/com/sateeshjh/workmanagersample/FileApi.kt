package com.sateeshjh.workmanagersample

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET

interface FileApi {

    @GET("/photo-1707327956851-30a531b70cda?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D.jpg")
    suspend fun downloadImage(): Response<ResponseBody>

    companion object {

        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("https://images.unsplash.com")
                .build()
                .create(FileApi::class.java)
        }
    }
}