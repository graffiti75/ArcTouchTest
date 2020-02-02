package com.arctouch.codechallenge.model.api

import android.util.Log
import com.arctouch.codechallenge.AppConfiguration
import com.arctouch.codechallenge.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    fun create(): TmdbApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(TmdbApi.URL)
            .client(interceptor())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit.create(TmdbApi::class.java)
    }

    private fun interceptor(): OkHttpClient {
        val logging = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.d(AppConfiguration.TAG, it)
        })
        logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}