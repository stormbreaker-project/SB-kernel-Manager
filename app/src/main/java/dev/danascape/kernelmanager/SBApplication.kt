package dev.danascape.kernelmanager

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder
import dev.danascape.kernelmanager.core.di.AppContainer
import dev.danascape.kernelmanager.core.di.AppContainerOwner

class SBApplication : Application(), AppContainerOwner, SingletonImageLoader.Factory {

    override val appContainer: AppContainer by lazy { AppContainer(this) }

    /** Every news cover the site publishes is an SVG, which Coil cannot decode without this. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
                add(OkHttpNetworkFetcherFactory())
            }
            .build()
}
