// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DiscoverRoute

fun NavController.navigateToDiscover(navOptions: NavOptions? = null) = navigate(DiscoverRoute, navOptions)

fun NavGraphBuilder.discoverScreen(
    contentPadding: PaddingValues,
    onOpenDevice: () -> Unit,
    onOpenMonitoring: () -> Unit,
    onOpenNews: () -> Unit,
) {
    composable<DiscoverRoute> {
        DiscoverScreen(
            contentPadding = contentPadding,
            onOpenDevice = onOpenDevice,
            onOpenMonitoring = onOpenMonitoring,
            onOpenNews = onOpenNews,
        )
    }
}
