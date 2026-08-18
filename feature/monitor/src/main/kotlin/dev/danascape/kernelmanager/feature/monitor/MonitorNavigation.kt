package dev.danascape.kernelmanager.feature.monitor

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object MonitorRoute

fun NavController.navigateToMonitor(navOptions: NavOptions? = null) =
    navigate(MonitorRoute, navOptions)

fun NavGraphBuilder.monitorScreen(contentPadding: PaddingValues) {
    composable<MonitorRoute> { MonitorScreen(contentPadding = contentPadding) }
}
