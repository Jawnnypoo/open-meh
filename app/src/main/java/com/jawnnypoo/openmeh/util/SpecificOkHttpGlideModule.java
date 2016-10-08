package com.jawnnypoo.openmeh.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpGlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.jawnnypoo.openmeh.BuildConfig;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Provide a Specific {@link OkHttpClient} for Glide to use
 */
public class SpecificOkHttpGlideModule extends OkHttpGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));
        }
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(clientBuilder.build()));
    }
}
