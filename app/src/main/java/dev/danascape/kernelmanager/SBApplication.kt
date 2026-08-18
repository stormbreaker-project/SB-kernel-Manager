package dev.danascape.kernelmanager

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder
import dev.danascape.kernelmanager.core.data.links.LinksRepository
import dev.danascape.kernelmanager.core.data.news.NewsRepository
import dev.danascape.kernelmanager.core.data.settings.ThemeRepository
import dev.danascape.kernelmanager.core.network.createHttpClient
import io.ktor.client.HttpClient

/**
 * Manual dependency container.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    private val httpClient: HttpClient by lazy { createHttpClient(appContext.cacheDir) }

    val newsRepository: NewsRepository by lazy { NewsRepository(httpClient) }

    val linksRepository: LinksRepository by lazy {
        LinksRepository(httpClient, appContext.assets)
    }

    val themeRepository: ThemeRepository by lazy { ThemeRepository(appContext) }
}

class SBApplication : Application(), SingletonImageLoader.Factory {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
                add(OkHttpNetworkFetcherFactory())
            }
            .build()
}
