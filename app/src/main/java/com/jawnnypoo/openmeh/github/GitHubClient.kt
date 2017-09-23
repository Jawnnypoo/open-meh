package com.jawnnypoo.openmeh.github

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * GitHub!
 */
object GitHubClient {

    val API_URL = "https://api.github.com"

    interface GitHub {
        @GET("/repos/{owner}/{repo}/contributors")
        fun contributors(
                @Path("owner") owner: String,
                @Path("repo") repo: String): Single<List<Contributor>>
    }

    lateinit var gitHub: GitHub

    fun init() {
        val restAdapter = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(API_URL)
                .build()
        gitHub = restAdapter.create(GitHub::class.java)
    }

    fun contributors(owner: String, repo: String): Single<List<Contributor>> {
        return gitHub.contributors(owner, repo)
    }
}
