// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
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

fun NavController.navigateToDeviceInfo(navOptions: NavOptions? = null) = navigate(DeviceInfoRoute, navOptions)

fun NavController.navigateToSensors(navOptions: NavOptions? = null) = navigate(SensorsRoute, navOptions)

/**
 * The device screens, sharing one view model.
 *
 * Scoping it to the graph rather than each entry is what keeps the switch instant:
 * a per-screen view model would re-read the device on every tab press, and the
 * load sample alone holds the state empty for half a second.
 */
fun NavGraphBuilder.deviceGraph(
    navController: NavController,
    contentPadding: PaddingValues,
) {
    navigation<DeviceGraphRoute>(startDestination = DeviceInfoRoute) {
        composable<DeviceInfoRoute> { entry ->
            DeviceInfoScreen(
                contentPadding = contentPadding,
                viewModel = sharedDeviceViewModel(navController, entry),
            )
        }
        composable<SensorsRoute> { entry ->
            SensorsScreen(
                contentPadding = contentPadding,
                viewModel = sharedDeviceViewModel(navController, entry),
            )
        }
    }
}

@Composable
private fun sharedDeviceViewModel(
    navController: NavController,
    entry: NavBackStackEntry,
): DeviceInfoViewModel {
    val parent = remember(entry) { navController.getBackStackEntry(DeviceGraphRoute) }
    return viewModel(viewModelStoreOwner = parent, factory = DeviceInfoViewModel.Factory)
}
