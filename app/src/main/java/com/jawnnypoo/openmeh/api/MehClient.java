package com.jawnnypoo.openmeh.api;

import com.jawnnypoo.openmeh.BuildConfig;
import com.jawnnypoo.openmeh.api.rss.RSS;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;

/**
 * Meh-----.com?
 */
public class MehClient {
    private static final String API_KEY_VALUE = BuildConfig.MEH_API_KEY;
    private static final String API_URL = "https://api.meh.com";
    private static final String PARAM_API_KEY = "apikey";

    private static final String RSS_URL = "https://meh.com";

    private static Meh sMeh;
    private static MehRss sMehRss;

    public interface Meh {
        @GET("/1/current.json?" + PARAM_API_KEY + "=" + API_KEY_VALUE)
        Call<MehResponse> getMeh( );
    }

    public interface MehRss {
        @GET("/deals.rss")
        Call<RSS> getMehRss();
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

    public static MehRss rssInstance() {
        if (sMehRss == null) {
            OkHttpClient client = new OkHttpClient();
            Retrofit restAdapter = new Retrofit.Builder()
                    .baseUrl(RSS_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .client(client)
                    .build();
            sMehRss = restAdapter.create(MehRss.class);
        }
        return sMehRss;
    }
}
