// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.battery

import kotlinx.serialization.Serializable

/**
 * One reading of the battery and the clocks, taken together.
 *
 * The two clocks matter as much as the charge: their difference is how much of
 * the interval the device actually slept, which is the number that separates a
 * healthy idle from one held awake by a wakelock.
 */
@Serializable
data class BatterySample(
    /** SystemClock.elapsedRealtime — counts time spent suspended. */
    val elapsedMillis: Long,
    /** SystemClock.uptimeMillis — does not count suspend. */
    val awakeMillis: Long,
    val levelPercent: Int,
    /** Remaining charge in microamp-hours, or null where unreported. */
    val chargeMicroAmpHours: Int?,
    /** Instantaneous current in microamps; sign is vendor-dependent. */
    val currentMicroAmps: Int?,
    val voltageMilliVolts: Int?,
    val temperatureC: Float?,
    val charging: Boolean,
    val screenOn: Boolean,
)
