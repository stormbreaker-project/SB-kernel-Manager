// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.battery

// Drain is accumulated in tenths so a slow discharge does not round away.
private const val TENTHS_PER_PERCENT = 10

/**
 * Folds samples into a session.
 *
 * Deliberately pure: no Android, no clock of its own, no storage. Everything it
 * needs arrives in the sample, which is what makes the drain arithmetic
 * testable — and that arithmetic is the whole feature, so it needs to be.
 */
object BatteryTracker {
    /**
     * Attributes the interval between [previous] and [sample] to whichever
     * screen state it was spent in.
     *
     * Charging ends a session rather than being folded in: mixing a charge into
     * a discharge rate makes both meaningless. Reboots also restart it, since
     * elapsedRealtime resets and the interval would otherwise be negative.
     */
    fun fold(
        session: BatterySession?,
        previous: BatterySample?,
        sample: BatterySample,
    ): BatterySession {
        val rebooted = previous != null && sample.elapsedMillis < previous.elapsedMillis
        val unplugged = previous != null && previous.charging && !sample.charging

        // Any of these makes the accumulated totals meaningless to continue.
        val startsFresh = session == null || rebooted || unplugged || sample.charging
        if (startsFresh) return start(sample)
        if (previous == null) return session.copy(latest = sample)

        val elapsed = sample.elapsedMillis - previous.elapsedMillis
        if (elapsed <= 0) return session.copy(latest = sample)

        // The screen state at the start of the interval owns it. A transition
        // mid-interval is attributed to where it began, which is why the service
        // samples on the transition itself rather than only on a timer.
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
