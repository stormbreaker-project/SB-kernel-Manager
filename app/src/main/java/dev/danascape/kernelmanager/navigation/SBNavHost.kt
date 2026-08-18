package dev.danascape.kernelmanager.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import dev.danascape.kernelmanager.feature.builds.buildsScreen
import dev.danascape.kernelmanager.feature.builds.navigateToBuilds
import dev.danascape.kernelmanager.feature.devices.devicesScreen
import dev.danascape.kernelmanager.feature.devices.navigateToDevices
import dev.danascape.kernelmanager.feature.discover.DiscoverRoute
import dev.danascape.kernelmanager.feature.discover.discoverScreen
import dev.danascape.kernelmanager.feature.discover.navigateToDiscover
import dev.danascape.kernelmanager.feature.licenses.licensesScreen
import dev.danascape.kernelmanager.feature.licenses.navigateToLicenses
import dev.danascape.kernelmanager.feature.monitor.monitorScreen
import dev.danascape.kernelmanager.feature.monitor.navigateToMonitor
import dev.danascape.kernelmanager.feature.more.moreScreen
import dev.danascape.kernelmanager.feature.more.navigateToMore
import dev.danascape.kernelmanager.feature.news.navigateToNews
import dev.danascape.kernelmanager.feature.news.newsScreen
import dev.danascape.kernelmanager.feature.tune.navigateToTune
import dev.danascape.kernelmanager.feature.tune.tuneScreen

/**
 * Composes the feature graphs.
 *
 * Features never depend on each other, so anything cross-feature — More
 * opening News, Devices or Licenses — is wired here.
 *
 * @param contentPadding window insets including the floating nav bar. Screens
 *   apply these to their own content so scrolled content passes under the bar.
 */
@Composable
fun SBNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = DiscoverRoute,
        modifier = modifier,
    ) {
        discoverScreen(contentPadding)
        tuneScreen(contentPadding)
        monitorScreen(contentPadding)
        buildsScreen(contentPadding)
        moreScreen(
            contentPadding = contentPadding,
            onOpenDevices = { navController.navigateToDevices() },
            onOpenNews = { navController.navigateToNews() },
            onOpenLicenses = { navController.navigateToLicenses() },
        )

        // Reached from More rather than the bar.
        newsScreen(contentPadding)
        devicesScreen(contentPadding)
        licensesScreen(contentPadding)
    }
}

/**
 * Switches top-level tabs.
 *
 * Tabs are siblings, not a stack: re-selecting must not pile up entries, and
 * returning to a tab restores where the user left it.
 */
fun NavHostController.navigateToTopLevel(destination: SBDestination) {
    val options = navOptions {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
    when (destination) {
        SBDestination.DISCOVER -> navigateToDiscover(options)
        SBDestination.TUNE -> navigateToTune(options)
        SBDestination.MONITOR -> navigateToMonitor(options)
        SBDestination.BUILDS -> navigateToBuilds(options)
        SBDestination.MORE -> navigateToMore(options)
    }
}
