// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.monitor

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object MonitorRoute

/** Detail pushed from the CPU card. */
@Serializable
data object CpuDetailRoute

fun NavController.navigateToMonitor(navOptions: NavOptions? = null) = navigate(MonitorRoute, navOptions)

fun NavController.navigateToCpuDetail(navOptions: NavOptions? = null) = navigate(CpuDetailRoute, navOptions)

fun NavGraphBuilder.monitorScreen(
    contentPadding: PaddingValues,
    onOpenCpuDetail: () -> Unit,
) {
    composable<MonitorRoute> {
        MonitorScreen(contentPadding = contentPadding, onOpenCpuDetail = onOpenCpuDetail)
    }
}

fun NavGraphBuilder.cpuDetailScreen(contentPadding: PaddingValues) {
    composable<CpuDetailRoute> { CpuDetailScreen(contentPadding = contentPadding) }
}
