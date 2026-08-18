// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.theme

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import dev.danascape.kernelmanager.core.designsystem.R

/*
 * Type is resolved through the Google Fonts provider, so the two brand faces
 * add nothing to the APK.
 *
 * The provider is a GMS content provider, and a meaningful share of this
 * audience runs de-Googled ROMs where it is simply not installed. Compose's
 * own failure mode there is to fall back to the platform default sans — which
 * would quietly render codenames, kernel versions and stat readouts in a
 * proportional face, the one place this design cannot afford it. So provider
 * availability is checked up front and the fallback is chosen explicitly:
 * monospace stays monospace.
 */

private const val FONTS_AUTHORITY = "com.google.android.gms.fonts"

private val GoogleFontsProvider =
    GoogleFont.Provider(
        providerAuthority = FONTS_AUTHORITY,
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs,
    )

/**
 * Whether the Google Fonts provider is installed and visible to us.
 *
 * Package-visibility filtering applies from API 30, so this only answers
 * truthfully because the manifest declares a matching `<queries>` entry.
 */
private fun isFontProviderAvailable(context: Context): Boolean {
    val pm = context.packageManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.resolveContentProvider(FONTS_AUTHORITY, PackageManager.ComponentInfoFlags.of(0L))
    } else {
        @Suppress("DEPRECATION")
        pm.resolveContentProvider(FONTS_AUTHORITY, 0)
    } != null
}

private fun googleFontFamily(
    name: String,
    weights: List<FontWeight>,
): FontFamily {
    val font = GoogleFont(name)
    return FontFamily(weights.map { Font(googleFont = font, fontProvider = GoogleFontsProvider, weight = it) })
}

/** Headings. Matches `h1, h2, h3, .brand-name` on the website. */
private fun displayFamily(available: Boolean): FontFamily =
    if (available) {
        googleFontFamily(
            "Space Grotesk",
            listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold),
        )
    } else {
        FontFamily.SansSerif
    }

/**
 * Monospace is a first-class face here, not a code-block afterthought: it
 * carries codenames, kernel versions, dates, and stat readouts. It is what
 * makes the product read as engineer-built — hence the monospace fallback.
 */
private fun monoFamily(available: Boolean): FontFamily =
    if (available) {
        googleFontFamily(
            "JetBrains Mono",
            listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.Bold),
        )
    } else {
        FontFamily.Monospace
    }

/*
 * Body copy stays on the platform sans in both cases. The site uses Inter, but
 * at body sizes the platform grotesque is near-indistinguishable, and keeping
 * body text local means the bulk of the UI renders identically whether or not
 * the provider answered.
 */
private val Body = FontFamily.SansSerif

internal fun sbTypography(context: Context): Typography {
    val available = isFontProviderAvailable(context)
    return sbTypography(display = displayFamily(available), mono = monoFamily(available))
}

internal fun sbTypography(
    display: FontFamily,
    mono: FontFamily,
): Typography =
    Typography(
        displayLarge =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.Bold,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-1.0).sp,
            ),
        displayMedium =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp,
                lineHeight = 52.sp,
                letterSpacing = (-0.5).sp,
            ),
        displaySmall =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = (-0.25).sp,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = (-0.5).sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                letterSpacing = (-0.25).sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = display,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = Body,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = Body,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.15.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = Body,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.2.sp,
            ),
        // Label styles are the mono channel: kickers, codenames, versions, stats.
        labelLarge =
            TextStyle(
                fontFamily = mono,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = mono,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = mono,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
    )
