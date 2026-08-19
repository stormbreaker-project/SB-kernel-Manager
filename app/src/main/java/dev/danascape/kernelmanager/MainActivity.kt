// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.danascape.kernelmanager.core.data.settings.ThemeRepository
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.ThemePreference
import dev.danascape.kernelmanager.ui.SBApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val themeRepository = (application as SBApplication).appContainer.themeRepository

        setContent {
            val darkTheme = isDarkTheme(themeRepository)
            SystemBarIcons(darkTheme)
            SBTheme(darkTheme = darkTheme) {
                SBApp()
            }
        }
    }
}

/** The bars are transparent, so their icons have to follow the app's theme, not the system's. */
@Composable
private fun SystemBarIcons(darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as Activity).window
    LaunchedEffect(darkTheme) {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

/** Seeded from the system so the first frame matches the launch window. */
@Composable
private fun isDarkTheme(repository: ThemeRepository): Boolean {
    val systemDark = isSystemInDarkTheme()
    val preference by repository.theme.collectAsStateWithLifecycle(
        initialValue = ThemePreference.SYSTEM,
    )
    return when (preference) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }
}
