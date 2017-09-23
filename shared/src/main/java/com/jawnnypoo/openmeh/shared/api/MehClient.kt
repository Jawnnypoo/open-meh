package com.jawnnypoo.openmeh.shared.api

import com.jawnnypoo.openmeh.shared.response.MehResponse
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * meh.com
 */
class MehClient private constructor() {

    companion object {

        const val API_URL = "https://api.meh.com/"
        const val PARAM_API_KEY = "apikey"

        fun create(apiKey: String, debug: Boolean): MehClient {
            val client = MehClient()
            val clientBuilder = OkHttpClient.Builder()
            if (debug) {
                clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
            clientBuilder.addInterceptor { chain ->
                val url = chain.request().url()
                        .newBuilder()
                        .addQueryParameter(PARAM_API_KEY, apiKey)
                        .build()
                val request = chain.request().newBuilder()
                        .url(url)
                        .build()
                chain.proceed(request)
            }
            val restAdapter = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build()
            client.mehService = restAdapter.create(MehService::class.java)
            return client
        }
    }

    interface MehService {
        @GET("1/current.json")
        fun getMeh(): Single<MehResponse>
    }

    private lateinit var mehService: MehService

    fun getMeh(): Single<MehResponse> {
        return mehService.getMeh()
    }
}
