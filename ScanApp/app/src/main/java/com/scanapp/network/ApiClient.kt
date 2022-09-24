package com.scanapp.network

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*
import java.util.concurrent.TimeUnit

object ApiClient
{

    private const val BASE_URL_TESTING = "https://api-ite.nmvo.eu/"

    private lateinit var retrofit: Retrofit
    lateinit var okHttpClient: OkHttpClient
    fun client(contxt: Context): Retrofit
    {
        val clientBuilder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
        val loggingInterceptor = HttpLoggingInterceptor()

        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)
        clientBuilder.addInterceptor(NetworkInterceptor(contxt))
        retrofit = Retrofit.Builder().baseUrl(getURL()).client(clientBuilder.build()).addConverterFactory(GsonConverterFactory.create()).build()
        return retrofit
    }

    fun client(): Retrofit
    {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)
        retrofit = Retrofit.Builder().baseUrl(getURL()).client(clientBuilder.build()).addConverterFactory(GsonConverterFactory.create()).build()
        return retrofit
    }

    private fun getURL(): String
    {
      return BASE_URL_TESTING
    }

}