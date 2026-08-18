// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
            SBTheme(darkTheme = isDarkTheme(themeRepository)) {
                SBApp()
            }
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
