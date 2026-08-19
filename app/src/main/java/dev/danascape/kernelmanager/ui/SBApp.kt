// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
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

/** How far the fade reaches below the status bar before it is fully transparent. */
private val FadeDepth = 14.dp

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
        Box(modifier = Modifier.fillMaxSize()) {
            SBNavHost(
                navController = navController,
                contentPadding = innerPadding,
            )
            StatusBarFade()
        }
    }
}

/**
 * Fades content out under the status bar rather than letting it cut off at a hard edge.
 *
 * The bar is transparent and the lists scroll beneath it, so without this a card
 * simply vanishes mid-row on the way up.
 */
@Composable
private fun BoxScope.StatusBarFade() {
    val background = MaterialTheme.colorScheme.background
    val inset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val total = inset + FadeDepth
    Spacer(
        modifier =
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(total)
                .background(
                    Brush.verticalGradient(
                        0f to background,
                        // Solid across the bar itself, so anything pinned just below
                        // it keeps its own colour rather than being tinted.
                        inset / total to background,
                        1f to background.copy(alpha = 0f),
                    ),
                ),
    )
}
