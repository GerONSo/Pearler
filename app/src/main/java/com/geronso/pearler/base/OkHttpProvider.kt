package com.geronso.pearler.base

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

object OkHttpProvider {
    fun provideClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()
}