package com.jawnnypoo.openmeh.api

import com.jawnnypoo.openmeh.BuildConfig
import com.jawnnypoo.openmeh.shared.api.MehResponse

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * meh.com
 */
class MehClient private constructor() {

    companion object {
        const val API_KEY_VALUE = BuildConfig.OPEN_MEH_MEH_API_KEY
        const val API_URL = "https://api.meh.com"
        const val PARAM_API_KEY = "apikey"

        fun create(): MehClient {
            val client = MehClient()
            val clientBuilder = OkHttpClient.Builder()
            if (BuildConfig.DEBUG) {
                clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
            val restAdapter = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build()
            client.mehService = restAdapter.create(MehService::class.java)
            return client
        }
    }

    interface MehService {
        @GET("/1/current.json?$PARAM_API_KEY=$API_KEY_VALUE")
        fun getMeh(): Single<MehResponse>
    }

    private lateinit var mehService: MehService

    fun getMeh(): Single<MehResponse> {
        return mehService.getMeh()
    }
}
