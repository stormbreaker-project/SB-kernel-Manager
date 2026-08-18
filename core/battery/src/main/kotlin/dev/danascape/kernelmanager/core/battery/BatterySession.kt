// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.battery

import kotlinx.serialization.Serializable

/**
 * What has happened since the charger came out.
 *
 * Screen-on and screen-off are accounted separately because they answer
 * different questions: active drain is what the user is spending, idle drain is
 * what the device is leaking. Averaging them hides both.
 *
 * A session resets on unplug rather than on boot, so the figures describe one
 * discharge rather than a mixture of charging and discharging.
 */
@Serializable
data class BatterySession(
    val startedAtElapsedMillis: Long,
    val startedAtLevelPercent: Int,
    val startedAtChargeMicroAmpHours: Int?,

    val screenOnMillis: Long = 0,
    val screenOffMillis: Long = 0,

    /** Tenths of a percent, to survive accumulation without rounding to nothing. */
    val screenOnDrainedTenths: Int = 0,
    val screenOffDrainedTenths: Int = 0,

    val screenOnDrainedMicroAmpHours: Long = 0,
    val screenOffDrainedMicroAmpHours: Long = 0,

    /** Suspended and awake time, counted only while the screen was off. */
    val screenOffDeepSleepMillis: Long = 0,
    val screenOffAwakeMillis: Long = 0,

    val latest: BatterySample? = null,
) {
    val screenOnDrainedPercent: Float get() = screenOnDrainedTenths / 10f
    val screenOffDrainedPercent: Float get() = screenOffDrainedTenths / 10f

    /** Percent per hour while the screen was on, or null before there is enough of a window. */
    val activeDrainPerHour: Float? get() = ratePerHour(screenOnDrainedPercent, screenOnMillis)

    /** Percent per hour while the screen was off. */
    val idleDrainPerHour: Float? get() = ratePerHour(screenOffDrainedPercent, screenOffMillis)

    /** How long this discharge has been running. */
    val sessionMillis: Long
        get() = ((latest?.elapsedMillis ?: startedAtElapsedMillis) - startedAtElapsedMillis)
            .coerceAtLeast(0)

    val screenOffDeepSleepFraction: Float
        get() = if (screenOffMillis > 0) screenOffDeepSleepMillis.toFloat() / screenOffMillis else 0f

    /** Power now, in watts, from current and voltage. */
    val watts: Float?
        get() {
            val amps = latest?.currentMicroAmps?.let { kotlin.math.abs(it) / 1_000_000f } ?: return null
            val volts = latest?.voltageMilliVolts?.let { it / 1000f } ?: return null
            return amps * volts
        }

    /**
     * Rough time until empty, from the rate the current screen state is draining
     * at. An estimate of ours — the platform exposes no discharge prediction.
     */
    val estimatedMillisRemaining: Long?
        get() {
            val sample = latest ?: return null
            if (sample.charging) return null
            val rate = (if (sample.screenOn) activeDrainPerHour else idleDrainPerHour) ?: return null
            if (rate <= 0f) return null
            return ((sample.levelPercent / rate) * 3_600_000L).toLong()
        }

    private companion object {
        /** Below this the rate is noise: one percent step over a few seconds extrapolates absurdly. */
        const val MIN_WINDOW_MILLIS = 120_000L

        fun ratePerHour(drainedPercent: Float, windowMillis: Long): Float? {
            if (windowMillis < MIN_WINDOW_MILLIS || drainedPercent <= 0f) return null
            return drainedPercent / (windowMillis / 3_600_000f)
        }
    }
}
