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

    /** Versioned: installed apps are not updated in step with the site. */
    const val NEWS: String = "$BASE_URL/api/v1/news.json"
    const val LINKS: String = "$BASE_URL/api/v1/links.json"
}

/**
 * Unknown keys are ignored so the site can add fields to a v1 payload without
 * breaking installs already in the field.
 */
val SBJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

private const val HTTP_CACHE_BYTES = 8L * 1024 * 1024
private const val CONNECT_TIMEOUT_MS = 15_000L
private const val REQUEST_TIMEOUT_MS = 30_000L

/**
 * The endpoints are static files behind an ETag, so OkHttp's disk cache is the
 * whole persistence story: repeat opens revalidate cheaply, and offline reads
 * come from here rather than failing.
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
