package dev.danascape.kernelmanager.core.data.news

import dev.danascape.kernelmanager.core.model.LoadError
import dev.danascape.kernelmanager.core.model.NewsPost
import dev.danascape.kernelmanager.core.network.SBEndpoints
import dev.danascape.kernelmanager.core.network.dto.NewsFeedDto
import dev.danascape.kernelmanager.core.network.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.io.IOException

/** Outcome of a newsroom load. */
sealed interface NewsResult {
    /**
     * @param stale true when this came from the HTTP cache because the network
     *   was unreachable, so the UI can say so rather than implying it is live.
     */
    data class Ok(val posts: List<NewsPost>, val stale: Boolean) : NewsResult

    data class Failed(val error: LoadError) : NewsResult
}

/**
 * Reads the newsroom from the static feed the website publishes.
 *
 * There is no local database. The feed is a small, immutable-per-deploy file
 * behind an ETag, so OkHttp's disk cache is the whole persistence story:
 * revalidation is a conditional request, and an unreachable network falls back
 * to the last good copy.
 */
class NewsRepository(private val client: HttpClient) {

    suspend fun news(): NewsResult = withContext(Dispatchers.IO) {
        try {
            val feed: NewsFeedDto = client.get(SBEndpoints.NEWS).body()
            NewsResult.Ok(feed.toDomain(), stale = false)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (io: IOException) {
            // Unreachable, not broken. Serve what we already have, if anything.
            cachedNews() ?: NewsResult.Failed(LoadError.OFFLINE)
        } catch (malformed: SerializationException) {
            NewsResult.Failed(LoadError.MALFORMED)
        } catch (other: Exception) {
            NewsResult.Failed(LoadError.SERVER)
        }
    }

    /**
     * Re-requests the feed with `only-if-cached`, which OkHttp answers from
     * disk or refuses outright — it never touches the network.
     */
    private suspend fun cachedNews(): NewsResult.Ok? = try {
        val feed: NewsFeedDto = client.get(SBEndpoints.NEWS) {
            header(HttpHeaders.CacheControl, "only-if-cached, max-stale=${Int.MAX_VALUE}")
        }.body()
        NewsResult.Ok(feed.toDomain(), stale = true)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        null
    }
}
