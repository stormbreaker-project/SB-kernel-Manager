package dev.danascape.kernelmanager.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.danascape.kernelmanager.feature.devices.DevicesScreen
import dev.danascape.kernelmanager.feature.downloads.DownloadsScreen
import dev.danascape.kernelmanager.feature.licenses.LicensesScreen
import dev.danascape.kernelmanager.feature.more.MoreScreen
import dev.danascape.kernelmanager.feature.news.NewsScreen

@Composable
fun SBNavHost(
    navController: NavHostController,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = SBRoute.News,
        modifier = modifier,
    ) {
        composable<SBRoute.News> { NewsScreen(contentPadding = contentPadding) }
        composable<SBRoute.Devices> { DevicesScreen(contentPadding = contentPadding) }
        composable<SBRoute.Downloads> { DownloadsScreen(contentPadding = contentPadding) }
        composable<SBRoute.More> {
            MoreScreen(
                contentPadding = contentPadding,
                onOpenLicenses = { navController.navigate(SBRoute.Licenses) },
            )
        }
        composable<SBRoute.Licenses> { LicensesScreen(contentPadding = contentPadding) }
    }
}

/**
 * Switches top-level tabs.
 *
 * Tabs are siblings, not a stack: re-selecting one must not pile up entries,
 * and coming back to a tab should restore where the user left it rather than
 * resetting to the top.
 */
fun NavHostController.navigateToTopLevel(destination: SBDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
