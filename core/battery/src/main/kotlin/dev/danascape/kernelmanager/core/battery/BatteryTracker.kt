// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.battery

private const val TENTHS_PER_PERCENT = 10

/** Folds samples into a session. */
object BatteryTracker {
    /** Attributes the interval to the screen state it was spent in. */
    fun fold(
        session: BatterySession?,
        previous: BatterySample?,
        sample: BatterySample,
    ): BatterySession {
        val rebooted = previous != null && sample.elapsedMillis < previous.elapsedMillis
        val unplugged = previous != null && previous.charging && !sample.charging

        val startsFresh = session == null || rebooted || unplugged || sample.charging
        if (startsFresh) return start(sample)
        if (previous == null) return session.copy(latest = sample)

        val elapsed = sample.elapsedMillis - previous.elapsedMillis
        if (elapsed <= 0) return session.copy(latest = sample)

        val wasScreenOn = previous.screenOn

        val drainedTenths =
            ((previous.levelPercent - sample.levelPercent) * TENTHS_PER_PERCENT).coerceAtLeast(0)
        val drainedUah = drainedMicroAmpHours(previous, sample)
        val awake = (sample.awakeMillis - previous.awakeMillis).coerceIn(0, elapsed)
        val slept = (elapsed - awake).coerceAtLeast(0)

        return if (wasScreenOn) {
            session.copy(
                screenOnMillis = session.screenOnMillis + elapsed,
                screenOnDrainedTenths = session.screenOnDrainedTenths + drainedTenths,
                screenOnDrainedMicroAmpHours = session.screenOnDrainedMicroAmpHours + drainedUah,
                latest = sample,
            )
        } else {
            session.copy(
                screenOffMillis = session.screenOffMillis + elapsed,
                screenOffDrainedTenths = session.screenOffDrainedTenths + drainedTenths,
                screenOffDrainedMicroAmpHours = session.screenOffDrainedMicroAmpHours + drainedUah,
                screenOffDeepSleepMillis = session.screenOffDeepSleepMillis + slept,
                screenOffAwakeMillis = session.screenOffAwakeMillis + awake,
                latest = sample,
            )
        }
    }

    fun start(sample: BatterySample): BatterySession =
        BatterySession(
            startedAtElapsedMillis = sample.elapsedMillis,
            startedAtLevelPercent = sample.levelPercent,
            startedAtChargeMicroAmpHours = sample.chargeMicroAmpHours,
            latest = sample,
        )

    /** Charge counters only go down while discharging; a rise means a top-up, not negative drain. */
    private fun drainedMicroAmpHours(
        previous: BatterySample,
        sample: BatterySample,
    ): Long {
        val before = previous.chargeMicroAmpHours ?: return 0
        val after = sample.chargeMicroAmpHours ?: return 0
        return (before - after).toLong().coerceAtLeast(0)
    }
}
