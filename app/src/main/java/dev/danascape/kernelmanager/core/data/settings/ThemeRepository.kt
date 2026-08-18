package dev.danascape.kernelmanager.core.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.danascape.kernelmanager.core.model.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

private val ThemeKey = stringPreferencesKey("theme")

class ThemeRepository(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    val theme: Flow<ThemePreference> = dataStore.data
        // A corrupt or unreadable store must not take the whole UI down; the
        // system default is always a safe answer.
        .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
        .map { preferences ->
            preferences[ThemeKey]
                ?.let { stored -> ThemePreference.entries.firstOrNull { it.name == stored } }
                ?: ThemePreference.SYSTEM
        }

    suspend fun setTheme(preference: ThemePreference) {
        dataStore.edit { it[ThemeKey] = preference.name }
    }
}
