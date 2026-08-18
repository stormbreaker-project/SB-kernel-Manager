package dev.danascape.kernelmanager

import android.app.Application
import android.content.Context
import dev.danascape.kernelmanager.core.data.news.NewsRepository
import dev.danascape.kernelmanager.core.network.createHttpClient
import io.ktor.client.HttpClient

/**
 * Manual dependency container.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    private val httpClient: HttpClient by lazy { createHttpClient(appContext.cacheDir) }

    val newsRepository: NewsRepository by lazy { NewsRepository(httpClient) }
}

class SBApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
