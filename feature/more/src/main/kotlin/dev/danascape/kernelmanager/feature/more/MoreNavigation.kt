package dev.danascape.kernelmanager.feature.more

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object MoreRoute

fun NavController.navigateToMore(navOptions: NavOptions? = null) = navigate(MoreRoute, navOptions)

/**
 * @param onOpenLicenses supplied by :app. Features never navigate to each
 *   other directly, so cross-feature destinations arrive as callbacks.
 */
fun NavGraphBuilder.moreScreen(
    contentPadding: PaddingValues,
    onOpenLicenses: () -> Unit,
) {
    composable<MoreRoute> {
        MoreScreen(contentPadding = contentPadding, onOpenLicenses = onOpenLicenses)
    }
}
