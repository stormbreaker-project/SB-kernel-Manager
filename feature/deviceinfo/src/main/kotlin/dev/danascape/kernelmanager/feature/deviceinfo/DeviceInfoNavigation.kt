// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object DeviceInfoRoute

fun NavController.navigateToDeviceInfo(navOptions: NavOptions? = null) = navigate(DeviceInfoRoute, navOptions)

fun NavGraphBuilder.deviceInfoScreen(contentPadding: PaddingValues) {
    composable<DeviceInfoRoute> {
        DeviceInfoScreen(contentPadding = contentPadding)
    }
}
