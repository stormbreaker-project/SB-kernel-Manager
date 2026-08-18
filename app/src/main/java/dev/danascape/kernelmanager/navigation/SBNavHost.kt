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
import dev.danascape.kernelmanager.feature.devices.navigateToDevices
import dev.danascape.kernelmanager.feature.discover.DiscoverRoute
import dev.danascape.kernelmanager.feature.discover.discoverScreen
import dev.danascape.kernelmanager.feature.licenses.licensesScreen
import dev.danascape.kernelmanager.feature.licenses.navigateToLicenses
import dev.danascape.kernelmanager.feature.monitor.monitorScreen
import dev.danascape.kernelmanager.feature.monitor.navigateToMonitor
import dev.danascape.kernelmanager.feature.more.MoreRoute
import dev.danascape.kernelmanager.feature.more.moreScreen
import dev.danascape.kernelmanager.feature.news.navigateToNews
import dev.danascape.kernelmanager.feature.news.newsScreen
import dev.danascape.kernelmanager.feature.tune.tuneScreen
import kotlinx.serialization.Serializable

/**
 * More's nested graph.
 *
 * The graph route is what the nav bar tracks, so anything reached from More —
 * News, Devices, Licenses — keeps More selected instead of dropping the bar
 * back to the start destination. It also gives the tab its own back stack, so
 * leaving and returning lands where the user left off.
 *
 * Owned by :app because grouping several features is a composition concern;
 * the features themselves still only know their own routes.
 */
@Serializable
data object MoreGraphRoute

/**
 * Composes the feature graphs.
 *
 * Features never depend on each other, so anything cross-feature is wired here.
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
        discoverScreen(
            contentPadding = contentPadding,
            onOpenMonitoring = { navController.navigateToMonitor() },
            onOpenNews = { navController.navigateToNews() },
        )
        tuneScreen(contentPadding)
        monitorScreen(contentPadding)
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

/**
 * Switches top-level tabs.
 *
 * Tabs are siblings, not a stack: re-selecting must not pile up entries, and
 * returning to a tab restores where the user left it.
 */
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
