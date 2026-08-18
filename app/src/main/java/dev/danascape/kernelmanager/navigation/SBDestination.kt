// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.danascape.kernelmanager.R
import dev.danascape.kernelmanager.feature.builds.BuildsRoute
import dev.danascape.kernelmanager.feature.discover.DiscoverRoute
import dev.danascape.kernelmanager.feature.tune.TuneRoute

/** Top-level destinations, in nav bar order. */
enum class SBDestination(
    val route: Any,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:StringRes val contentDescriptionRes: Int,
    @param:DrawableRes val expandedIconRes: Int? = null,
    @param:StringRes val expandedLabelRes: Int? = null,
) {
    DISCOVER(
        route = DiscoverRoute,
        iconRes = R.drawable.ic_tab_discover,
        labelRes = R.string.tab_discover,
        contentDescriptionRes = R.string.tab_discover_description,
    ),
    TUNE(
        route = TuneRoute,
        iconRes = R.drawable.ic_tab_tune,
        labelRes = R.string.tab_tune,
        contentDescriptionRes = R.string.tab_tune_description,
    ),
    MONITOR(
        route = MonitorGraphRoute,
        iconRes = R.drawable.ic_tab_monitor,
        labelRes = R.string.tab_monitor,
        contentDescriptionRes = R.string.tab_monitor_description,
    ),
    BUILDS(
        route = BuildsRoute,
        iconRes = R.drawable.ic_tab_builds,
        labelRes = R.string.tab_builds,
        contentDescriptionRes = R.string.tab_builds_description,
    ),
    MORE(
        route = MoreGraphRoute,
        iconRes = R.drawable.ic_tab_more,
        labelRes = R.string.tab_more,
        contentDescriptionRes = R.string.tab_more_description,
    ),
}

/** The top-level destination this entry sits under. */
fun NavDestination?.topLevelDestination(): SBDestination? {
    val hierarchy = this?.hierarchy ?: return null
    return SBDestination.entries.firstOrNull { destination ->
        hierarchy.any { it.hasRoute(destination.route::class) }
    }
}
