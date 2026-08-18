package dev.danascape.kernelmanager.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.danascape.kernelmanager.R
import dev.danascape.kernelmanager.feature.devices.DevicesRoute
import dev.danascape.kernelmanager.feature.downloads.DownloadsRoute
import dev.danascape.kernelmanager.feature.more.MoreRoute
import dev.danascape.kernelmanager.feature.news.NewsRoute
import kotlin.reflect.KClass

/**
 * Top-level destinations, in nav bar order.
 *
 * Ordinal is the tab index, so this order is the on-screen order; nothing else
 * may depend on it. Routes themselves are owned by the feature modules.
 */
enum class SBDestination(
    val route: KClass<*>,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:StringRes val contentDescriptionRes: Int,
    @param:DrawableRes val expandedIconRes: Int? = null,
    @param:StringRes val expandedLabelRes: Int? = null,
) {
    NEWS(
        route = NewsRoute::class,
        iconRes = R.drawable.ic_tab_news,
        labelRes = R.string.tab_news,
        contentDescriptionRes = R.string.tab_news_description,
    ),
    DEVICES(
        route = DevicesRoute::class,
        iconRes = R.drawable.ic_tab_devices,
        labelRes = R.string.tab_devices,
        contentDescriptionRes = R.string.tab_devices_description,
    ),
    DOWNLOADS(
        route = DownloadsRoute::class,
        iconRes = R.drawable.ic_tab_downloads,
        labelRes = R.string.tab_downloads,
        contentDescriptionRes = R.string.tab_downloads_description,
    ),
    MORE(
        route = MoreRoute::class,
        iconRes = R.drawable.ic_tab_more,
        labelRes = R.string.tab_more,
        contentDescriptionRes = R.string.tab_more_description,
    ),
}

/**
 * The top-level destination this entry sits under.
 *
 * Walks the hierarchy, so a screen pushed inside a tab still resolves to it.
 */
fun NavDestination?.topLevelDestination(): SBDestination? {
    val hierarchy = this?.hierarchy ?: return null
    return SBDestination.entries.firstOrNull { destination ->
        hierarchy.any { it.hasRoute(destination.route) }
    }
}
