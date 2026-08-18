// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarSizes
import com.prauga.pvot.designsystem.theme.PvotAppTheme

/** The app's theme: StormBreaker's brand palette and type carried on the Pvot design system. */
@Composable
fun SBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) SBDarkColorScheme else SBLightColorScheme
    val extendedColors = if (darkTheme) SBDarkExtendedColors else SBLightExtendedColors

    CompositionLocalProvider(LocalSBExtendedColors provides extendedColors) {
        PvotAppTheme(
            darkTheme = darkTheme,
            dynamicColor = false,
            colorScheme = colorScheme,
            typography = SBTypography,
            navBarColors =
                PvotNavBarColors(
                    gradient = SolidColor(colorScheme.primary),
                    collapsedChipColor = Color.Transparent,
                    containerColor = colorScheme.surface,
                    iconSelectedColor = colorScheme.onPrimary,
                    iconUnselectedColor = extendedColors.muted,
                    rippleColor = colorScheme.onSurface.copy(alpha = 0.2f),
                ),
            navBarSizes =
                PvotNavBarSizes(
                    barHeight = 68.dp,
                    collapsedItemSize = 46.dp,
                    cornerRadius = 100.dp,
                    itemCornerRadius = 26.dp,
                    collapsedIconSize = 20.dp,
                    expandedIconSize = 20.dp,
                    labelFontSize = 12.sp,
                    maxExpandedWidth = 150.dp,
                    horizontalPadding = 10.dp,
                    contentPaddingHorizontal = 8.dp,
                    itemSpacing = 3.dp,
                ),
            content = content,
        )
    }
}

/** Entry point for the brand tokens Material 3 has no slot for. */
object SBTheme {
    val colors: SBExtendedColors
        @Composable get() = LocalSBExtendedColors.current
}
