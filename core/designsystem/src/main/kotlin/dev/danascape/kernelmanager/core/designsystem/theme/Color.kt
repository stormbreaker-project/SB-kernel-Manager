// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkBg = Color(0xFF08080F)
private val DarkBg2 = Color(0xFF0D0D1A)
private val DarkSurface = Color(0xFF12121F)
private val DarkSurface2 = Color(0xFF16162A)
private val DarkBorder = Color(0xFF22223A)
private val DarkBorder2 = Color(0xFF30304E)
private val DarkText = Color(0xFFECECF5)
private val DarkMuted = Color(0xFF9A9AB5)
private val DarkFaint = Color(0xFF6B6B85)
private val DarkBrand = Color(0xFFC7E7FF)
private val DarkBrand2 = Color(0xFF4D8DFF)
private val DarkAccent = Color(0xFF35E0D4)
private val DarkBrandText = Color(0xFF0A1020)

private val LightBg = Color(0xFFF6F7FB)
private val LightBg2 = Color(0xFFEEF0F7)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurface2 = Color(0xFFF2F3FA)
private val LightBorder = Color(0xFFE3E4EF)
private val LightBorder2 = Color(0xFFD3D5E5)
private val LightText = Color(0xFF14142A)
private val LightMuted = Color(0xFF55566F)
private val LightFaint = Color(0xFF8687A0)
private val LightBrand = Color(0xFF2F6FE0)
private val LightAccent = Color(0xFF0F766E)
private val LightBrandText = Color(0xFFFFFFFF)

private val Ink = Color(0xFF0A0A12)

internal val SBDarkColorScheme =
    darkColorScheme(
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

internal val SBLightColorScheme =
    lightColorScheme(
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

/** Brand tokens that Material 3 has no slot for. */
@Immutable
data class SBExtendedColors(
    val brandSecondary: Color,
    val accent: Color,
    val muted: Color,
    val faint: Color,
    val hairline: Color,
    val hairlineStrong: Color,
    val ink: Color,
)

internal val SBDarkExtendedColors =
    SBExtendedColors(
        brandSecondary = DarkBrand2,
        accent = DarkAccent,
        muted = DarkMuted,
        faint = DarkFaint,
        hairline = DarkBorder,
        hairlineStrong = DarkBorder2,
        ink = Ink,
    )

internal val SBLightExtendedColors =
    SBExtendedColors(
        brandSecondary = DarkBrand2,
        accent = LightAccent,
        muted = LightMuted,
        faint = LightFaint,
        hairline = LightBorder,
        hairlineStrong = LightBorder2,
        ink = Ink,
    )

internal val LocalSBExtendedColors = staticCompositionLocalOf { SBDarkExtendedColors }
