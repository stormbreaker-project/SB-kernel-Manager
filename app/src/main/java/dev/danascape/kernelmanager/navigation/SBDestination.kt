package dev.danascape.kernelmanager.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.danascape.kernelmanager.R
import kotlinx.serialization.Serializable

/**
 * Routes, as types rather than strings.
 *
 * Phase 0's destinations take no arguments, so string routes would have done.
 * They are typed from the start because the ones that follow are not: the OTA
 * flow navigates to a specific device by codename, and a codename mistyped
 * into a string route fails at runtime rather than at compile time.
 */
sealed interface SBRoute {
    @Serializable
    data object News : SBRoute

    @Serializable
    data object Devices : SBRoute

    @Serializable
    data object Downloads : SBRoute

    @Serializable
    data object More : SBRoute

    /** Detail screen pushed from More; not a tab. */
    @Serializable
    data object Licenses : SBRoute
}

/**
 * The top-level destinations, declared in nav bar order.
 *
 * Ordinal is the nav bar's tab index, so the order here is the order on
 * screen; nothing else may depend on it.
 *
 * [expandedIconRes] and [expandedLabelRes] let the selected pill read as that
 * tab's primary action rather than repeating its name — the idiom PvotNavBar
 * is built around. Phase 0 is browse-only and has no per-tab action worth
 * promoting, so both stay null and the pill falls back to icon plus label.
 * Phase 1 uses it on Builds, where the action is "Update".
 */
enum class SBDestination(
    val route: SBRoute,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:StringRes val contentDescriptionRes: Int,
    @param:DrawableRes val expandedIconRes: Int? = null,
    @param:StringRes val expandedLabelRes: Int? = null,
) {
    NEWS(
        route = SBRoute.News,
        iconRes = R.drawable.ic_tab_news,
        labelRes = R.string.tab_news,
        contentDescriptionRes = R.string.tab_news_description,
    ),
    DEVICES(
        route = SBRoute.Devices,
        iconRes = R.drawable.ic_tab_devices,
        labelRes = R.string.tab_devices,
        contentDescriptionRes = R.string.tab_devices_description,
    ),
    DOWNLOADS(
        route = SBRoute.Downloads,
        iconRes = R.drawable.ic_tab_downloads,
        labelRes = R.string.tab_downloads,
        contentDescriptionRes = R.string.tab_downloads_description,
    ),
    MORE(
        route = SBRoute.More,
        iconRes = R.drawable.ic_tab_more,
        labelRes = R.string.tab_more,
        contentDescriptionRes = R.string.tab_more_description,
    ),
}

/**
 * The top-level destination this back stack entry sits under.
 *
 * Walks the hierarchy rather than matching the destination itself, so a screen
 * pushed inside a tab still resolves to that tab.
 */
fun NavDestination?.topLevelDestination(): SBDestination? {
    val hierarchy = this?.hierarchy ?: return null
    return SBDestination.entries.firstOrNull { destination ->
        hierarchy.any { it.hasRoute(destination.route::class) }
    }
}
