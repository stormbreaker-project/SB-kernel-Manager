// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
data object DeviceGraphRoute

@Serializable
data object DeviceInfoRoute

@Serializable
data object SensorsRoute

@Serializable
data object TestsRoute

fun NavController.navigateToDeviceInfo(navOptions: NavOptions? = null) = navigate(DeviceInfoRoute, navOptions)

fun NavController.navigateToSensors(navOptions: NavOptions? = null) = navigate(SensorsRoute, navOptions)

fun NavController.navigateToTests(navOptions: NavOptions? = null) = navigate(TestsRoute, navOptions)

/**
 * The device screens.
 *
 * Each owns its own view model; what they share is the repository, which caches the
 * profile and the details read, so a tab switch costs no rereading of the device.
 */
fun NavGraphBuilder.deviceGraph(contentPadding: PaddingValues) {
    navigation<DeviceGraphRoute>(startDestination = DeviceInfoRoute) {
        composable<DeviceInfoRoute> {
            DeviceInfoScreen(contentPadding = contentPadding)
        }
        composable<SensorsRoute> {
            SensorsScreen(contentPadding = contentPadding)
        }
        composable<TestsRoute> {
            TestsScreen(contentPadding = contentPadding)
        }
    }
}
