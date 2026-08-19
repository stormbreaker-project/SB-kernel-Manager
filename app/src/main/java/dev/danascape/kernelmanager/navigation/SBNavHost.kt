// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import dev.danascape.kernelmanager.feature.builds.buildsScreen
import dev.danascape.kernelmanager.feature.devices.devicesScreen
import dev.danascape.kernelmanager.feature.deviceinfo.deviceInfoScreen
import dev.danascape.kernelmanager.feature.deviceinfo.navigateToDeviceInfo
import dev.danascape.kernelmanager.feature.devices.navigateToDevices
import dev.danascape.kernelmanager.feature.discover.DiscoverRoute
import dev.danascape.kernelmanager.feature.discover.discoverScreen
import dev.danascape.kernelmanager.feature.licenses.licensesScreen
import dev.danascape.kernelmanager.feature.licenses.navigateToLicenses
import dev.danascape.kernelmanager.feature.monitor.MonitorRoute
import dev.danascape.kernelmanager.feature.monitor.cpuDetailScreen
import dev.danascape.kernelmanager.feature.monitor.monitorScreen
import dev.danascape.kernelmanager.feature.monitor.navigateToCpuDetail
import dev.danascape.kernelmanager.feature.monitor.navigateToMonitor
import dev.danascape.kernelmanager.feature.more.MoreRoute
import dev.danascape.kernelmanager.feature.more.moreScreen
import dev.danascape.kernelmanager.feature.news.navigateToNews
import dev.danascape.kernelmanager.feature.news.newsScreen
import dev.danascape.kernelmanager.feature.tune.tuneScreen
import kotlinx.serialization.Serializable

/** Discover's nested graph, so the device screen keeps Discover selected. */
@Serializable
data object DiscoverGraphRoute

/** More's nested graph. */
@Serializable
data object MoreGraphRoute

/** Monitor's nested graph, so the CPU detail screen keeps Monitor selected. */
@Serializable
data object MonitorGraphRoute

/** Composes the feature graphs. */
@Composable
fun SBNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = DiscoverGraphRoute,
        modifier = modifier,
    ) {
        navigation<DiscoverGraphRoute>(startDestination = DiscoverRoute) {
            discoverScreen(
                contentPadding = contentPadding,
                onOpenDevice = { navController.navigateToDeviceInfo() },
                onOpenMonitoring = { navController.navigateToMonitor() },
                onOpenNews = { navController.navigateToNews() },
            )
            deviceInfoScreen(contentPadding)
        }
        tuneScreen(contentPadding)
        navigation<MonitorGraphRoute>(startDestination = MonitorRoute) {
            monitorScreen(
                contentPadding = contentPadding,
                onOpenCpuDetail = { navController.navigateToCpuDetail() },
            )
            cpuDetailScreen(contentPadding)
        }
        buildsScreen(contentPadding)

        navigation<MoreGraphRoute>(startDestination = MoreRoute) {
            moreScreen(
                contentPadding = contentPadding,
                onOpenDevices = { navController.navigateToDevices() },
                onOpenNews = { navController.navigateToNews() },
                onOpenLicenses = { navController.navigateToLicenses() },
            )
            newsScreen(contentPadding)
            devicesScreen(contentPadding)
            licensesScreen(contentPadding)
        }
    }
}

/** Switches top-level tabs. */
fun NavHostController.navigateToTopLevel(destination: SBDestination) {
    navigate(
        destination.route,
        navOptions {
            popUpTo(graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        },
    )
}
