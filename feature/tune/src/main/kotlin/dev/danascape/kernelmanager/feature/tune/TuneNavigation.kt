package dev.danascape.kernelmanager.feature.tune

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object TuneRoute

fun NavController.navigateToTune(navOptions: NavOptions? = null) =
    navigate(TuneRoute, navOptions)

fun NavGraphBuilder.tuneScreen(contentPadding: PaddingValues) {
    composable<TuneRoute> { TuneScreen(contentPadding = contentPadding) }
}
