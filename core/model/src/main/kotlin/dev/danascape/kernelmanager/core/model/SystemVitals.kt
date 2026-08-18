// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/** Thermal throttling severity. */
enum class ThermalStatus {
    NONE,
    LIGHT,
    MODERATE,
    SEVERE,
    CRITICAL,
    EMERGENCY,
    SHUTDOWN,
    UNKNOWN,
}

data class StorageVitals(
    val usedBytes: Long,
    val totalBytes: Long,
    val fileSystem: String?,
) {
    val usedFraction: Float get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f
}

data class NetworkVitals(
    val rxBytes: Long,
    val txBytes: Long,
)

/** How much of the time since boot the device actually slept. */
data class SleepStats(
    val elapsedMillis: Long,
    val awakeMillis: Long,
) {
    val deepSleepMillis: Long get() = (elapsedMillis - awakeMillis).coerceAtLeast(0)

    val deepSleepFraction: Float
        get() = if (elapsedMillis > 0) deepSleepMillis.toFloat() / elapsedMillis else 0f
}

/** Identification only. Usage and frequency live behind denied vendor nodes. */
data class GpuInfo(
    val vendor: String?,
    val renderer: String?,
    val glVersion: String?,
)
