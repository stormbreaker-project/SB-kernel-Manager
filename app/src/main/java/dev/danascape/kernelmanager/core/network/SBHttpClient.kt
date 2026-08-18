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

/** Where the static API lives. Published by the website's own deploy. */
object SBEndpoints {
    const val BASE_URL: String = "https://stormbreaker.squadri.me"

    /** Versioned path: installed apps cannot be updated in step with the site. */
    const val NEWS: String = "$BASE_URL/api/v1/news.json"
}

/**
 * Tolerant by design.
 *
 * The site can add fields to a v1 payload at any time, and installs in the
 * field are not updated in step with it. An unknown key must never be the
 * reason a user cannot read the news, so unknown keys are ignored rather than
 * fatal.
 */
val SBJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

private const val HTTP_CACHE_BYTES = 8L * 1024 * 1024
private const val CONNECT_TIMEOUT_MS = 15_000L
private const val REQUEST_TIMEOUT_MS = 30_000L

/**
 * Builds the app's single HTTP client.
 *
 * Uses the OkHttp engine specifically for its disk cache. The endpoints are
 * static files behind an ETag and `max-age`, so ordinary HTTP caching does the
 * work: repeat opens revalidate cheaply, and when the device is offline the
 * repository can still serve the last good response out of this cache instead
 * of showing an error over content it already has.
 */
fun createHttpClient(cacheDir: File): HttpClient = HttpClient(OkHttp) {
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
