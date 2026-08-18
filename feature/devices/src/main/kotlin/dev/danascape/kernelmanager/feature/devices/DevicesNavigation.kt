// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.devices

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DevicesRoute

fun NavController.navigateToDevices(navOptions: NavOptions? = null) = navigate(DevicesRoute, navOptions)

fun NavGraphBuilder.devicesScreen(contentPadding: PaddingValues) {
    composable<DevicesRoute> { DevicesScreen(contentPadding = contentPadding) }
}
