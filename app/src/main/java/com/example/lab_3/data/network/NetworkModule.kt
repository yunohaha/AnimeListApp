package com.example.lab_3.data.network

import okhttp3.OkHttpClient
import  retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
object NetworkModule {
    private const val BASE_URL = "https://api.jikan.moe/v4/"

    private val okHttpClient = OkHttpClient.Builder()
        .build()

    val api: JikanApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(JikanApi::class.java)

}