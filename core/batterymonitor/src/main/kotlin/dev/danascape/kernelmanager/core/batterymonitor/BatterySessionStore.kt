// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.batterymonitor

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.danascape.kernelmanager.core.battery.BatterySample
import dev.danascape.kernelmanager.core.battery.BatterySession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

private val Context.batteryDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "battery_monitor")

private val SessionKey = stringPreferencesKey("session")
private val PreviousSampleKey = stringPreferencesKey("previous_sample")
private val EnabledKey = booleanPreferencesKey("enabled")

/** Persists the session so it survives process death and reboot. */
class BatterySessionStore(
    context: Context,
) {
    private val dataStore = context.applicationContext.batteryDataStore

    private val json = Json { ignoreUnknownKeys = true }

    val enabled: Flow<Boolean> =
        dataStore.data
            .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
            .map { it[EnabledKey] ?: false }

    val session: Flow<BatterySession?> =
        dataStore.data
            .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
            .map { preferences -> preferences[SessionKey]?.let(::decodeSession) }

    suspend fun setEnabled(value: Boolean) {
        dataStore.edit { it[EnabledKey] = value }
    }

    suspend fun session(): BatterySession? = session.first()

    suspend fun previousSample(): BatterySample? =
        dataStore.data
            .first()[PreviousSampleKey]
            ?.let { stored ->
                runCatching { json.decodeFromString<BatterySample>(stored) }.getOrNull()
            }

    suspend fun save(
        session: BatterySession,
        sample: BatterySample,
    ) {
        dataStore.edit { preferences ->
            preferences[SessionKey] = json.encodeToString(session)
            preferences[PreviousSampleKey] = json.encodeToString(sample)
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(SessionKey)
            preferences.remove(PreviousSampleKey)
        }
    }

    /** A schema change must reset the session, not crash the service. */
    private fun decodeSession(stored: String): BatterySession? =
        runCatching { json.decodeFromString<BatterySession>(stored) }.getOrNull()
}
