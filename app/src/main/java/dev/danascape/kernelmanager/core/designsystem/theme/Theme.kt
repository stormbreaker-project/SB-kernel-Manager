package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import com.prauga.pvot.designsystem.components.navigation.PvotNavBarColors
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
            content = content,
        )
    }
}

/** Entry point for the brand tokens Material 3 has no slot for. */
object SBTheme {
    val colors: SBExtendedColors
        @Composable get() = LocalSBExtendedColors.current
}
