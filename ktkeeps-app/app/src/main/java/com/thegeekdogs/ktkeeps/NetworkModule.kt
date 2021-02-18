package com.thegeekdogs.ktkeeps

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            Log.d("NetworkModule", it.request().url().toString())
            return@addInterceptor it.call().execute()
        }
        .build()

    val retrofitInstance : Retrofit = Retrofit.Builder()
        .baseUrl("https://ktkeeps.heroku.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()
}