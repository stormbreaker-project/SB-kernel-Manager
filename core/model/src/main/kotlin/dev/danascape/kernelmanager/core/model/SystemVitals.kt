// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/**
 * Thermal throttling severity.
 *
 * Not a temperature. Exact degrees are denied to unprivileged apps, but the
 * framework does report how hard the device is being throttled, which is the
 * part a user can act on.
 */
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
    /** `f2fs` or `ext4`, from /proc/mounts. */
    val fileSystem: String?,
) {
    val usedFraction: Float get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f
}

data class NetworkVitals(
    val rxBytes: Long,
    val txBytes: Long,
)

/**
 * How much of the time since boot the device actually slept.
 *
 * Both clocks are public API and need no permission: elapsedRealtime counts
 * deep sleep, uptimeMillis does not, so the difference is sleep. This is the
 * one battery statistic that needs neither root nor BATTERY_STATS.
 */
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
