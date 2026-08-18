package dev.danascape.kernelmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.danascape.kernelmanager.core.data.settings.ThemePreference
import dev.danascape.kernelmanager.core.data.settings.ThemeRepository
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.ui.SBApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val themeRepository = (application as SBApplication).container.themeRepository

        setContent {
            SBTheme(darkTheme = rememberDarkTheme(themeRepository)) {
                SBApp()
            }
        }
    }
}

/**
 * Resolves the stored preference to a concrete light/dark choice.
 *
 * Seeded from the system so the first frame — before DataStore has read from
 * disk — matches what the launch window already painted, instead of flashing
 * the wrong theme and correcting itself.
 */
@Composable
private fun rememberDarkTheme(repository: ThemeRepository): Boolean {
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
