package com.jawnnypoo.openmeh.shared.api

import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * meh.com client
 */
class MehClient(
    apiKey: String,
    debug: Boolean = false
) {
    companion object {
        private const val API_URL = "https://meh.com/api/"
        private const val PARAM_API_KEY = "apikey"
    }

    private val mehService: MehService

    init {
        val clientBuilder = OkHttpClient.Builder()
        if (debug) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            clientBuilder.addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            })
        }
        clientBuilder.addInterceptor { chain ->
            val url = chain.request()
                .url
                .newBuilder()
                .addQueryParameter(PARAM_API_KEY, apiKey)
                .build()
            val request = chain.request().newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val restAdapter = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(clientBuilder.build())
            .build()
        mehService = restAdapter.create(MehService::class.java)
    }

    /**
     * The meh deal of the day!
     */
    suspend fun meh(): MehResponse {
        return mehService.meh()
    }
}
