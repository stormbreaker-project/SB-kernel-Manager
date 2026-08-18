package dev.danascape.kernelmanager.core.data.news

import dev.danascape.kernelmanager.core.common.DataResult
import dev.danascape.kernelmanager.core.common.LoadError
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

/**
 * The newsroom, read from the site's static feed.
 *
 * No database: the feed is a small file behind an ETag, so the HTTP cache is
 * the persistence layer.
 */
class NewsRepository(private val client: HttpClient) {

    suspend fun news(): DataResult<List<NewsPost>> = withContext(Dispatchers.IO) {
        try {
            DataResult.Success(client.get(SBEndpoints.NEWS).body<NewsFeedDto>().toDomain())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (io: IOException) {
            cached() ?: DataResult.Failure(LoadError.OFFLINE)
        } catch (malformed: SerializationException) {
            DataResult.Failure(LoadError.MALFORMED)
        } catch (other: Exception) {
            DataResult.Failure(LoadError.SERVER)
        }
    }

    /** `only-if-cached` is answered from disk or refused; it never hits the network. */
    private suspend fun cached(): DataResult.Success<List<NewsPost>>? = try {
        val feed: NewsFeedDto = client.get(SBEndpoints.NEWS) {
            header(HttpHeaders.CacheControl, "only-if-cached, max-stale=${Int.MAX_VALUE}")
        }.body()
        DataResult.Success(feed.toDomain(), stale = true)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        null
    }
}
