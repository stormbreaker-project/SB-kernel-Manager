// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder
import dev.danascape.kernelmanager.core.batterymonitor.BatteryMonitorService
import dev.danascape.kernelmanager.core.di.AppContainer
import dev.danascape.kernelmanager.core.di.AppContainerOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SBApplication : Application(), AppContainerOwner, SingletonImageLoader.Factory {

    override val appContainer: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        restoreBatteryMonitor()
    }

    /**
     * Second line of defence behind the restart receiver.
     *
     * A background start can be refused depending on which exemption applies to
     * the broadcast, and there is no notification to tell the user it did not
     * come back. Opening the app is a foreground moment, where starting is
     * always permitted, so anything the receiver missed recovers here.
     */
    private fun restoreBatteryMonitor() {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            if (BatteryMonitorService.running) return@launch
            if (appContainer.batterySessionStore.enabled.first()) {
                BatteryMonitorService.start(this@SBApplication)
            }
        }
    }

    /** Every news cover the site publishes is an SVG, which Coil cannot decode without this. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
                add(OkHttpNetworkFetcherFactory())
            }
            .build()
}
