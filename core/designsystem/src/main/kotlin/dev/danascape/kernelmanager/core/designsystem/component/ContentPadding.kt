package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Adds a screen's own spacing on top of the window insets it was handed.
 *
 * The nav bar floats, so screens are given the scaffold's insets rather than
 * being clipped above it: a scrollable applies the result as `contentPadding`
 * and its content passes under the bar instead of stopping at a hard edge,
 * while still coming to rest clear of it.
 *
 * Start and end are carried through rather than assumed zero — in landscape a
 * display cutout puts real insets there.
 */
@Composable
fun PaddingValues.expandedBy(
    horizontal: Dp = 0.dp,
    top: Dp = 0.dp,
    bottom: Dp = 0.dp,
): PaddingValues {
    val direction = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(direction) + horizontal,
        end = calculateEndPadding(direction) + horizontal,
        top = calculateTopPadding() + top,
        bottom = calculateBottomPadding() + bottom,
    )
}
