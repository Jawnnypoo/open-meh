package com.jawnnypoo.openmeh.github

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * GitHub!
 */
class GitHubClient(debug: Boolean = false) {

    companion object {
        private const val API_URL = "https://api.github.com/"
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
                        override fun log(message: String) = println("GitHubClient: $message")
                    }
                    level = LogLevel.BODY
                }
            }
            defaultRequest {
                url(API_URL)
            }
        }
    }

    suspend fun contributors(owner: String, repo: String): List<Contributor> {
        return client.get("repos/$owner/$repo/contributors").body()
    }
}
