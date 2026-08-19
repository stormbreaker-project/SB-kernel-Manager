// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.danascape.kernelmanager.R
import dev.danascape.kernelmanager.feature.deviceinfo.DeviceInfoRoute
import dev.danascape.kernelmanager.feature.deviceinfo.SensorsRoute

/** The nav bar swaps to these while the device screens are open. */
enum class DeviceDestination(
    val route: Any,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:StringRes val contentDescriptionRes: Int,
) {
    HARDWARE(
        route = DeviceInfoRoute,
        iconRes = R.drawable.ic_tab_hardware,
        labelRes = R.string.tab_device_hw,
        contentDescriptionRes = R.string.tab_device_hw_description,
    ),
    SENSORS(
        route = SensorsRoute,
        iconRes = R.drawable.ic_tab_sensors,
        labelRes = R.string.tab_sensors,
        contentDescriptionRes = R.string.tab_sensors_description,
    ),
}

/** The device destination this entry sits under, or null when outside the device graph. */
fun NavDestination?.deviceDestination(): DeviceDestination? {
    val hierarchy = this?.hierarchy ?: return null
    return DeviceDestination.entries.firstOrNull { destination ->
        hierarchy.any { it.hasRoute(destination.route::class) }
    }
}
