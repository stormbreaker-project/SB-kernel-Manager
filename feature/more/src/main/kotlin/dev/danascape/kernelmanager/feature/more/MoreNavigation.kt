// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

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

/** Cross-feature destinations arrive as callbacks, supplied by :app. */
fun NavGraphBuilder.moreScreen(
    contentPadding: PaddingValues,
    onOpenDevices: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    composable<MoreRoute> {
        MoreScreen(
            contentPadding = contentPadding,
            onOpenDevices = onOpenDevices,
            onOpenNews = onOpenNews,
            onOpenLicenses = onOpenLicenses,
        )
    }
}
