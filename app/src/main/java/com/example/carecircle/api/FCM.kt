package com.example.carecircle.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FCM {

    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient()
                        .create()
                )
            )
            .build()

    }

    val api: ApiServer by lazy {
        retrofit.create(ApiServer::class.java)
    }

}