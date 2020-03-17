package com.jawnnypoo.openmeh.shared.api

import com.jawnnypoo.openmeh.shared.response.MehResponse
import io.reactivex.Single
import retrofit2.http.GET

interface MehService {
    @GET("1/current.json")
    fun meh(): Single<MehResponse>
}