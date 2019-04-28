package com.arctouch.codechallenge.api

import com.arctouch.codechallenge.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * TmdbNetwork
 *
 * Create network object to make reques for server
 */
object TmdbNetwork {

    fun create(): TmdbApi {

        val requestInterceptor = Interceptor { chain ->

            val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("api_key", BuildConfig.API_KEY)
                    .addQueryParameter("language", BuildConfig.DEFAULT_LANGUAGE)
                    .build()

            val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        return retrofit.create(TmdbApi::class.java)
    }
}