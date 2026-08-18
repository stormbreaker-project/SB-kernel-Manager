package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarSizes
import com.prauga.pvot.designsystem.theme.PvotAppTheme

/**
 * The app's theme: StormBreaker's brand palette and type carried on the Pvot
 * design system.
 *
 * Material You is deliberately off. The kernel and the site are one product
 * with one identity, and a wallpaper-derived palette would break the match —
 * and would put unreviewed colors behind readouts where color means something
 * (a thermal reading, a failed flash).
 */
@Composable
fun SBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) SBDarkColorScheme else SBLightColorScheme
    val extendedColors = if (darkTheme) SBDarkExtendedColors else SBLightExtendedColors

    // Resolving the font families touches PackageManager, so do it once per
    // context rather than on every recomposition.
    val context = LocalContext.current
    val typography = remember(context) { sbTypography(context) }

    CompositionLocalProvider(LocalSBExtendedColors provides extendedColors) {
        PvotAppTheme(
            darkTheme = darkTheme,
            dynamicColor = false,
            colorScheme = colorScheme,
            typography = typography,
            // Flat by default: a solid brand fill for the selected pill and no
            // chip behind unselected ones. Pvot's gradient is a Brush, so a
            // SolidColor is how "no gradient" is expressed.
            navBarColors = PvotNavBarColors(
                gradient = SolidColor(colorScheme.primary),
                collapsedChipColor = Color.Transparent,
                containerColor = colorScheme.surface,
                iconSelectedColor = colorScheme.onPrimary,
                iconUnselectedColor = extendedColors.muted,
            ),
            // barWidth/expandedWidth are left unset so the bar measures
            // intrinsically. A fixed 367dp bar plus 22dp padding a side needs
            // a 411dp viewport and would clip on the 360dp-wide devices a lot
            // of this fleet still runs.
            //
            // labelFontSize drops from the 16sp default because the label text
            // style here is monospace, which sets appreciably wider.
            navBarSizes = PvotNavBarSizes(
                barHeight = 72.dp,
                collapsedItemSize = 56.dp,
                cornerRadius = 100.dp,
                itemCornerRadius = 28.dp,
                collapsedIconSize = 22.dp,
                expandedIconSize = 22.dp,
                labelFontSize = 14.sp,
                maxExpandedWidth = 180.dp,
                horizontalPadding = 22.dp,
                contentPaddingHorizontal = 9.dp,
                itemSpacing = 4.dp,
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
