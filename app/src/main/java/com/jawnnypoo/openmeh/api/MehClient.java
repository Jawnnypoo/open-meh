package com.jawnnypoo.openmeh.api;

import com.jawnnypoo.openmeh.BuildConfig;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;

/**
 * Meh
 * Created by John on 4/17/2015.
 */
public class MehClient {
    private static final String API_KEY_VALUE = BuildConfig.MEH_API_KEY;
    private static final String API_URL = "https://api.meh.com";
    private static final String PARAM_API_KEY = "apikey";

    private static Meh mMeh;

    public interface Meh {
        @GET("/1/current.json?" + PARAM_API_KEY + "=" + API_KEY_VALUE)
        Call<MehResponse> getMeh( );
    }

    public static Meh instance() {
        if (mMeh == null) {
            OkHttpClient client = new OkHttpClient();
            if (BuildConfig.DEBUG) {
                client.networkInterceptors().add(new TimberRequestInterceptor());
            }
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            mMeh = restAdapter.create(Meh.class);
        }
        return mMeh;
    }
}
