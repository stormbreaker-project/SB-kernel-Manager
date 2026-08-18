// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.battery

import kotlinx.serialization.Serializable

private const val TENTHS_PER_PERCENT = 10f
private const val MICRO_PER_UNIT = 1_000_000f
private const val MILLI_PER_UNIT = 1000f
private const val MILLIS_PER_HOUR = 3_600_000L

@Serializable
data class BatterySession(
    val startedAtElapsedMillis: Long,
    val startedAtLevelPercent: Int,
    val startedAtChargeMicroAmpHours: Int?,
    val screenOnMillis: Long = 0,
    val screenOffMillis: Long = 0,
    val screenOnDrainedTenths: Int = 0,
    val screenOffDrainedTenths: Int = 0,
    val screenOnDrainedMicroAmpHours: Long = 0,
    val screenOffDrainedMicroAmpHours: Long = 0,
    val screenOffDeepSleepMillis: Long = 0,
    val screenOffAwakeMillis: Long = 0,
    val latest: BatterySample? = null,
) {
    val screenOnDrainedPercent: Float get() = screenOnDrainedTenths / TENTHS_PER_PERCENT
    val screenOffDrainedPercent: Float get() = screenOffDrainedTenths / TENTHS_PER_PERCENT

    val activeDrainPerHour: Float? get() = ratePerHour(screenOnDrainedPercent, screenOnMillis)

    val idleDrainPerHour: Float? get() = ratePerHour(screenOffDrainedPercent, screenOffMillis)

    val sessionMillis: Long
        get() =
            ((latest?.elapsedMillis ?: startedAtElapsedMillis) - startedAtElapsedMillis)
                .coerceAtLeast(0)

    val screenOffDeepSleepFraction: Float
        get() = if (screenOffMillis > 0) screenOffDeepSleepMillis.toFloat() / screenOffMillis else 0f

    val watts: Float?
        get() {
            val amps = latest?.currentMicroAmps?.let { kotlin.math.abs(it) / MICRO_PER_UNIT } ?: return null
            val volts = latest?.voltageMilliVolts?.let { it / MILLI_PER_UNIT } ?: return null
            return amps * volts
        }

    val estimatedMillisRemaining: Long?
        get() {
            val sample = latest ?: return null
            if (sample.charging) return null
            val rate = (if (sample.screenOn) activeDrainPerHour else idleDrainPerHour) ?: return null
            if (rate <= 0f) return null
            return ((sample.levelPercent / rate) * MILLIS_PER_HOUR).toLong()
        }

    private companion object {
        const val MIN_WINDOW_MILLIS = 120_000L

        fun ratePerHour(
            drainedPercent: Float,
            windowMillis: Long,
        ): Float? {
            if (windowMillis < MIN_WINDOW_MILLIS || drainedPercent <= 0f) return null
            return drainedPercent / (windowMillis.toFloat() / MILLIS_PER_HOUR)
        }
    }
}
