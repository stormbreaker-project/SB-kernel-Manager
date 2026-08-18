package dev.danascape.kernelmanager.feature.downloads

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DownloadsRoute

fun NavController.navigateToDownloads(navOptions: NavOptions? = null) =
    navigate(DownloadsRoute, navOptions)

fun NavGraphBuilder.downloadsScreen(contentPadding: PaddingValues) {
    composable<DownloadsRoute> { DownloadsScreen(contentPadding = contentPadding) }
}
