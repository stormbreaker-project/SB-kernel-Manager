// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prauga.pvot.designsystem.components.navigation.PvotNavBar
import com.prauga.pvot.designsystem.components.navigation.PvotTabItem
import dev.danascape.kernelmanager.navigation.DeviceDestination
import dev.danascape.kernelmanager.navigation.SBDestination
import dev.danascape.kernelmanager.navigation.SBNavHost
import dev.danascape.kernelmanager.navigation.deviceDestination
import dev.danascape.kernelmanager.navigation.navigateToDevice
import dev.danascape.kernelmanager.navigation.navigateToTopLevel
import dev.danascape.kernelmanager.navigation.topLevelDestination

/** The app shell: one nav bar over the top-level destinations. */
@Composable
fun SBApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val device = backStackEntry?.destination.deviceDestination()
    val current = backStackEntry?.destination.topLevelDestination() ?: SBDestination.DISCOVER

    val deviceTabs =
        remember {
            DeviceDestination.entries.map {
                PvotTabItem(
                    iconRes = it.iconRes,
                    labelRes = it.labelRes,
                    contentDescriptionRes = it.contentDescriptionRes,
                )
            }
        }

    val tabs =
        remember {
            SBDestination.entries.map {
                PvotTabItem(
                    iconRes = it.iconRes,
                    labelRes = it.labelRes,
                    contentDescriptionRes = it.contentDescriptionRes,
                    expandedIconRes = it.expandedIconRes,
                    expandedLabelRes = it.expandedLabelRes,
                )
            }
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (device != null) {
                PvotNavBar(
                    selectedTab = device.ordinal,
                    onTabClick = { index -> navController.navigateToDevice(DeviceDestination.entries[index]) },
                    tabs = deviceTabs,
                )
            } else {
                PvotNavBar(
                    selectedTab = current.ordinal,
                    onTabClick = { index -> navController.navigateToTopLevel(SBDestination.entries[index]) },
                    tabs = tabs,
                )
            }
        },
    ) { innerPadding ->
        SBNavHost(
            navController = navController,
            contentPadding = innerPadding,
        )
    }
}
