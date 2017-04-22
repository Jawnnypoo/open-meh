package com.jawnnypoo.openmeh.util

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.jawnnypoo.openmeh.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.InputStream

/**
 * Provide a Specific [OkHttpClient] for Glide to use
 */
class SpecificOkHttpGlideModule : OkHttpGlideModule() {

    override fun registerComponents(context: Context?, glide: Glide) {
        val clientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        }
        glide.register(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(clientBuilder.build()))
    }
}
