package com.jawnnypoo.openmeh.shared.api

import com.jawnnypoo.openmeh.shared.response.MehResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * meh.com client
 */
class MehClient(
    apiKey: String,
    debug: Boolean = false
) {
    companion object {
        private const val API_URL = "https://meh.com/api/"
        private const val PARAM_API_KEY = "apikey"
    }

    private val client: HttpClient

    init {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(json)
            }
            if (debug) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) = println("MehClient: $message")
                    }
                    level = LogLevel.BODY
                }
            }
            defaultRequest {
                url.takeFrom(API_URL)
                url.parameters.append(PARAM_API_KEY, apiKey)
            }
        }
    }

    /**
     * The meh deal of the day!
     */
    suspend fun meh(): MehResponse {
        return client.get("1/current.json").body()
    }
}
