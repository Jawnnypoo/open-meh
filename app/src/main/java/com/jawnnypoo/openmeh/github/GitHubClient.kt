package com.jawnnypoo.openmeh.github

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * GitHub!
 */
object GitHubClient {

    private const val API_URL = "https://api.github.com"

    private lateinit var gitHub: GitHub

    fun init() {
        val restAdapter = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(API_URL)
            .build()
        gitHub = restAdapter.create(GitHub::class.java)
    }

    suspend fun contributors(owner: String, repo: String): List<Contributor> {
        return gitHub.contributors(owner, repo)
    }
}
