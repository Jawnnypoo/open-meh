package com.jawnnypoo.openmeh.api;

import com.jawnnypoo.openmeh.BuildConfig;
import com.jawnnypoo.openmeh.api.rss.RSS;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.SimpleXmlConverterFactory;
import retrofit.http.GET;

/**
 * Meh-----.com?
 * Created by John on 4/17/2015.
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
            if (BuildConfig.DEBUG) {
                client.networkInterceptors().add(new TimberRequestInterceptor());
            }
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
            if (BuildConfig.DEBUG) {
                client.networkInterceptors().add(new TimberRequestInterceptor());
            }
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
