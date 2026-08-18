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
            // Sized for five tabs on a 360dp screen, which is the tightest
            // case this fleet still runs. The expanded pill is the variable:
            // icon + 8dp gap + label + 32dp padding, and the label is
            // monospace, which sets wider than a proportional face. "Discover"
            // at 12sp is the worst case and lands the bar around 350dp.
            navBarSizes = PvotNavBarSizes(
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
