// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Cache
import java.io.File

/** Static endpoints published by the website's own deploy. */
object SBEndpoints {
    const val BASE_URL: String = BuildConfig.API_BASE_URL

    const val NEWS: String = "$BASE_URL/api/v1/news.json"
    const val LINKS: String = "$BASE_URL/api/v1/links.json"
}

val SBJson: Json =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

private const val HTTP_CACHE_BYTES = 8L * 1024 * 1024
private const val CONNECT_TIMEOUT_MS = 15_000L
private const val REQUEST_TIMEOUT_MS = 30_000L

/** Static files behind an ETag, so OkHttp's disk cache is the persistence. */
fun createHttpClient(cacheDir: File): HttpClient =
    HttpClient(OkHttp) {
        expectSuccess = true

        engine {
            config {
                cache(Cache(File(cacheDir, "http"), HTTP_CACHE_BYTES))
                retryOnConnectionFailure(true)
            }
        }

        install(ContentNegotiation) { json(SBJson) }

        install(HttpTimeout) {
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
        }

        defaultRequest {
            header(HttpHeaders.UserAgent, "SBKernelManager-Android")
        }
    }
