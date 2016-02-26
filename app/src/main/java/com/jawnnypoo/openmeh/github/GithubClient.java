package com.jawnnypoo.openmeh.github;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * GitHub!
 */
public class GithubClient {

    public static final String API_URL = "https://api.github.com";

    public interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        Call<List<Contributor>> contributors(
                @Path("owner") String owner,
                @Path("repo") String repo);
    }

    private static GitHub mGithub;
    public static GitHub instance() {
        if (mGithub == null) {
            Retrofit restAdapter = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(API_URL)
                    .build();
            mGithub = restAdapter.create(GitHub.class);
        }
        return mGithub;
    }
}
