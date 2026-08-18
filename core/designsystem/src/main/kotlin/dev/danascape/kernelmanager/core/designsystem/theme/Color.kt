// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/*
 * Brand palette, mirroring the website's src/styles/tokens.css.
 *
 * Token names below match the CSS custom properties one-for-one so that a
 * change on either side is traceable to the other; the app and the site are
 * meant to read as one product. Dark is the primary theme in both.
 */

// --- dark tokens ---------------------------------------------------------
private val DarkBg = Color(0xFF08080F)          // --bg
private val DarkBg2 = Color(0xFF0D0D1A)         // --bg-2
private val DarkSurface = Color(0xFF12121F)     // --surface
private val DarkSurface2 = Color(0xFF16162A)    // --surface-2
private val DarkBorder = Color(0xFF22223A)      // --border
private val DarkBorder2 = Color(0xFF30304E)     // --border-2
private val DarkText = Color(0xFFECECF5)        // --text
private val DarkMuted = Color(0xFF9A9AB5)       // --muted
private val DarkFaint = Color(0xFF6B6B85)       // --faint
private val DarkBrand = Color(0xFFC7E7FF)       // --brand
private val DarkBrand2 = Color(0xFF4D8DFF)      // --brand-2
private val DarkAccent = Color(0xFF35E0D4)      // --accent
private val DarkBrandText = Color(0xFF0A1020)   // --brand-text

// --- light tokens --------------------------------------------------------
private val LightBg = Color(0xFFF6F7FB)         // --bg
private val LightBg2 = Color(0xFFEEF0F7)        // --bg-2
private val LightSurface = Color(0xFFFFFFFF)    // --surface
private val LightSurface2 = Color(0xFFF2F3FA)   // --surface-2
private val LightBorder = Color(0xFFE3E4EF)     // --border
private val LightBorder2 = Color(0xFFD3D5E5)    // --border-2
private val LightText = Color(0xFF14142A)       // --text
private val LightMuted = Color(0xFF55566F)      // --muted
private val LightFaint = Color(0xFF8687A0)      // --faint
private val LightBrand = Color(0xFF2F6FE0)      // --brand
private val LightAccent = Color(0xFF0F766E)     // --accent
private val LightBrandText = Color(0xFFFFFFFF)  // --brand-text

/** --ink: the near-black terminal surface, identical in both themes. */
private val Ink = Color(0xFF0A0A12)

internal val SBDarkColorScheme = darkColorScheme(
    primary = DarkBrand,
    onPrimary = DarkBrandText,
    primaryContainer = Color(0xFF1A2B40),
    onPrimaryContainer = DarkBrand,
    secondary = DarkBrand2,
    onSecondary = Color(0xFF061024),
    secondaryContainer = Color(0xFF15233F),
    onSecondaryContainer = Color(0xFFB9D2FF),
    tertiary = DarkAccent,
    onTertiary = Color(0xFF00201D),
    tertiaryContainer = Color(0xFF0C332F),
    onTertiaryContainer = Color(0xFF6FF3E9),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0000),
    errorContainer = Color(0xFF4A0F0F),
    onErrorContainer = Color(0xFFFFD9D6),
    background = DarkBg,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurface2,
    onSurfaceVariant = DarkMuted,
    surfaceContainerLowest = Color(0xFF05050A),
    surfaceContainerLow = DarkBg2,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurface2,
    surfaceContainerHighest = Color(0xFF1C1C33),
    outline = DarkBorder2,
    outlineVariant = DarkBorder,
    inverseSurface = DarkText,
    inverseOnSurface = LightText,
    inversePrimary = LightBrand,
    scrim = Color(0xFF000000),
)

internal val SBLightColorScheme = lightColorScheme(
    primary = LightBrand,
    onPrimary = LightBrandText,
    primaryContainer = Color(0xFFDCE8FF),
    onPrimaryContainer = Color(0xFF072250),
    secondary = Color(0xFF3B5BA5),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE4F7),
    onSecondaryContainer = Color(0xFF0C1A38),
    tertiary = LightAccent,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC9F2EC),
    onTertiaryContainer = Color(0xFF00201D),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = LightBg,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightSurface2,
    onSurfaceVariant = LightMuted,
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAFBFE),
    surfaceContainer = LightSurface2,
    surfaceContainerHigh = LightBg2,
    surfaceContainerHighest = Color(0xFFE8EAF3),
    outline = LightBorder2,
    outlineVariant = LightBorder,
    inverseSurface = LightText,
    inverseOnSurface = Color(0xFFF4F4FA),
    inversePrimary = DarkBrand,
    scrim = Color(0xFF000000),
)

/**
 * Brand tokens that Material 3 has no slot for.
 *
 * Material's scheme covers roles, not the editorial distinctions this design
 * leans on — the three-step text ramp (onSurface / muted / faint) and the
 * hairline rules that carry the flat, terminal-influenced look. Reach for
 * these through [dev.danascape.kernelmanager.core.designsystem.theme.SBTheme].
 */
@Immutable
data class SBExtendedColors(
    /** --brand-2: the saturated blue, for links and secondary emphasis. */
    val brandSecondary: Color,
    /** --accent: teal, reserved for "live"/active signals. */
    val accent: Color,
    /** --muted: secondary body text. */
    val muted: Color,
    /** --faint: tertiary text — timestamps, counts, disabled states. */
    val faint: Color,
    /** --border: default hairline. */
    val hairline: Color,
    /** --border-2: stronger hairline, for dividers that need to read. */
    val hairlineStrong: Color,
    /** --ink: near-black terminal surface, identical in both themes. */
    val ink: Color,
)

internal val SBDarkExtendedColors = SBExtendedColors(
    brandSecondary = DarkBrand2,
    accent = DarkAccent,
    muted = DarkMuted,
    faint = DarkFaint,
    hairline = DarkBorder,
    hairlineStrong = DarkBorder2,
    ink = Ink,
)

internal val SBLightExtendedColors = SBExtendedColors(
    brandSecondary = DarkBrand2,
    accent = LightAccent,
    muted = LightMuted,
    faint = LightFaint,
    hairline = LightBorder,
    hairlineStrong = LightBorder2,
    ink = Ink,
)

internal val LocalSBExtendedColors = staticCompositionLocalOf { SBDarkExtendedColors }
