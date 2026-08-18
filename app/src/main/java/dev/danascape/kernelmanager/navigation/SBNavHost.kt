package dev.danascape.kernelmanager.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import dev.danascape.kernelmanager.feature.devices.devicesScreen
import dev.danascape.kernelmanager.feature.devices.navigateToDevices
import dev.danascape.kernelmanager.feature.downloads.downloadsScreen
import dev.danascape.kernelmanager.feature.downloads.navigateToDownloads
import dev.danascape.kernelmanager.feature.licenses.licensesScreen
import dev.danascape.kernelmanager.feature.licenses.navigateToLicenses
import dev.danascape.kernelmanager.feature.more.moreScreen
import dev.danascape.kernelmanager.feature.more.navigateToMore
import dev.danascape.kernelmanager.feature.news.NewsRoute
import dev.danascape.kernelmanager.feature.news.navigateToNews
import dev.danascape.kernelmanager.feature.news.newsScreen

/**
 * Composes the feature graphs.
 *
 * Features never depend on each other, so anything cross-feature — More
 * opening Licenses — is wired here.
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
        startDestination = NewsRoute,
        modifier = modifier,
    ) {
        newsScreen(contentPadding)
        devicesScreen(contentPadding)
        downloadsScreen(contentPadding)
        moreScreen(
            contentPadding = contentPadding,
            onOpenLicenses = { navController.navigateToLicenses() },
        )
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
        SBDestination.NEWS -> navigateToNews(options)
        SBDestination.DEVICES -> navigateToDevices(options)
        SBDestination.DOWNLOADS -> navigateToDownloads(options)
        SBDestination.MORE -> navigateToMore(options)
    }
}
