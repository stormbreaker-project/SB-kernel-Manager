package dev.danascape.kernelmanager.core.data.settings

import dev.danascape.kernelmanager.core.datastore.SettingsStore
import dev.danascape.kernelmanager.core.model.ThemePreference
import kotlinx.coroutines.flow.Flow

class ThemeRepository(private val store: SettingsStore) {

    val theme: Flow<ThemePreference> = store.theme

    suspend fun setTheme(preference: ThemePreference) = store.setTheme(preference)
}
