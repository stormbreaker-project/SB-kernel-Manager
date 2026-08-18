package dev.danascape.kernelmanager.core.data.links

import android.content.res.AssetManager
import dev.danascape.kernelmanager.core.model.LinkSection
import dev.danascape.kernelmanager.core.network.SBEndpoints
import dev.danascape.kernelmanager.core.network.SBJson
import dev.danascape.kernelmanager.core.network.dto.LinksDto
import dev.danascape.kernelmanager.core.network.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val BUNDLED_ASSET = "links.json"

/**
 * Community and project links.
 *
 * Published by the website so a moved Telegram invite is a site deploy rather
 * than an app release, with a copy bundled in the APK as the floor. The bundled
 * copy is what makes this screen work on first run with no network — it is
 * allowed to go stale, because the network copy wins whenever it is reachable.
 *
 * There is deliberately no failure path: something is always available.
 */
class LinksRepository(
    private val client: HttpClient,
    private val assets: AssetManager,
) {

    suspend fun links(): List<LinkSection> = withContext(Dispatchers.IO) {
        remote() ?: bundled()
    }

    private suspend fun remote(): List<LinkSection>? = try {
        client.get(SBEndpoints.LINKS).body<LinksDto>().toDomain().takeIf { it.isNotEmpty() }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        null
    }

    private fun bundled(): List<LinkSection> = try {
        assets.open(BUNDLED_ASSET).bufferedReader().use { reader ->
            SBJson.decodeFromString<LinksDto>(reader.readText()).toDomain()
        }
    } catch (_: Exception) {
        // Only reachable if the APK asset is missing or malformed, which is a
        // build error rather than a runtime condition.
        emptyList()
    }
}
