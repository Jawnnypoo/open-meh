package com.jawnnypoo.openmeh.github

import retrofit2.http.GET
import retrofit2.http.Path

interface GitHub {
    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun contributors(
            @Path("owner") owner: String,
            @Path("repo") repo: String): List<Contributor>
}