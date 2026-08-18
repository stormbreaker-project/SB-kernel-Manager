package dev.danascape.kernelmanager.core.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.danascape.kernelmanager.core.data.device.DeviceRepository
import dev.danascape.kernelmanager.core.data.links.LinksRepository
import dev.danascape.kernelmanager.core.data.news.NewsRepository
import dev.danascape.kernelmanager.core.data.settings.ThemeRepository
import dev.danascape.kernelmanager.core.datastore.SettingsStore
import dev.danascape.kernelmanager.core.device.SystemVitalsReader
import dev.danascape.kernelmanager.core.network.createHttpClient
import io.ktor.client.HttpClient

/**
 * Hand-rolled dependency container.
 *
 * The graph is a handful of singletons with no scoping, which Hilt would not
 * simplify — it would only add KSP to the build. Everything is lazy so a cold
 * start pays for nothing it does not use.
 *
 * It lives here rather than in :app because features resolve their
 * dependencies from it and cannot depend on the application module.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    private val httpClient: HttpClient by lazy { createHttpClient(appContext.cacheDir) }

    val newsRepository: NewsRepository by lazy { NewsRepository(httpClient) }

    val deviceRepository: DeviceRepository by lazy {
        DeviceRepository(SystemVitalsReader(appContext))
    }

    val linksRepository: LinksRepository by lazy {
        LinksRepository(httpClient, appContext.assets)
    }

    val themeRepository: ThemeRepository by lazy { ThemeRepository(SettingsStore(appContext)) }
}

/** Implemented by the Application so features can reach the container. */
interface AppContainerOwner {
    val appContainer: AppContainer
}

/** The container for the current context, for use in ViewModel factories. */
fun Context.appContainer(): AppContainer =
    (applicationContext as AppContainerOwner).appContainer

@Composable
fun rememberAppContainer(): AppContainer = LocalContext.current.appContainer()
