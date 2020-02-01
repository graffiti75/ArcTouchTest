package com.arctouch.codechallenge.view.base

import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.arctouch.codechallenge.AppConfiguration
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.model.api.TmdbApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

abstract class BaseActivity : AppCompatActivity() {

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

    protected val api: TmdbApi = Retrofit.Builder()
        .baseUrl(TmdbApi.URL)
//        .client(OkHttpClient.Builder().build())
        .client(interceptor())
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(TmdbApi::class.java)
}
