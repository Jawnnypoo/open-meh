package com.jawnnypoo.openmeh.shared.api

import com.jawnnypoo.openmeh.shared.response.MehResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * meh.com client
 */
class MehClient(
        apiKey: String,
        debug: Boolean = false
) {
    companion object {
        private const val API_URL = "https://api.meh.com/"
        private const val PARAM_API_KEY = "apikey"
    }

    private val mehService: MehService

    init {
        val clientBuilder = OkHttpClient.Builder()
        if (debug) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            clientBuilder.addInterceptor(httpLoggingInterceptor.apply { httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY })
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
                .add(KotlinJsonAdapterFactory())
                .build()

        val restAdapter = Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder.build())
                .build()
        mehService = restAdapter.create(MehService::class.java)
    }

    fun getMeh(): Single<MehResponse> {
        return mehService.getMeh()
    }
}
