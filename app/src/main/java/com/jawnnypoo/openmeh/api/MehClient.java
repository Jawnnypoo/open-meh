package com.jawnnypoo.openmeh.api;

import com.jawnnypoo.openmeh.BuildConfig;
import com.jawnnypoo.openmeh.shared.api.MehResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Meh-----.com?
 */
public class MehClient {
    private static final String API_KEY_VALUE = BuildConfig.MEH_API_KEY;
    private static final String API_URL = "https://api.meh.com";
    private static final String PARAM_API_KEY = "apikey";

    private static Meh sMeh;

    public interface Meh {
        @GET("/1/current.json?" + PARAM_API_KEY + "=" + API_KEY_VALUE)
        Call<MehResponse> getMeh();
    }

    public static Meh instance() {
        if (sMeh == null) {
            OkHttpClient client = new OkHttpClient();
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
            sMeh = restAdapter.create(Meh.class);
        }
        return sMeh;
    }
}
