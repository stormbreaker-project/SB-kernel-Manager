package dev.danascape.kernelmanager.core.battery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private const val HOUR = 3_600_000L

class BatteryTrackerTest {

    private fun sample(
        elapsed: Long,
        awake: Long = elapsed,
        level: Int = 100,
        charge: Int? = null,
        screenOn: Boolean = false,
        charging: Boolean = false,
        current: Int? = null,
        voltage: Int? = null,
    ) = BatterySample(
        elapsedMillis = elapsed,
        awakeMillis = awake,
        levelPercent = level,
        chargeMicroAmpHours = charge,
        currentMicroAmps = current,
        voltageMilliVolts = voltage,
        temperatureC = null,
        charging = charging,
        screenOn = screenOn,
    )

    @Test
    fun `screen-on drain is attributed to active, not idle`() {
        val first = sample(0, level = 100, screenOn = true)
        val second = sample(HOUR, level = 90, screenOn = true)

        val session = BatteryTracker.fold(BatteryTracker.start(first), first, second)

        assertEquals(HOUR, session.screenOnMillis)
        assertEquals(0L, session.screenOffMillis)
        assertEquals(10f, session.screenOnDrainedPercent, 0.01f)
        assertEquals(10f, session.activeDrainPerHour!!, 0.01f)
        assertNull("idle rate should not exist yet", session.idleDrainPerHour)
    }

    @Test
    fun `screen-off drain is attributed to idle`() {
        val first = sample(0, level = 90, screenOn = false)
        val second = sample(2 * HOUR, level = 88, screenOn = false)

        val session = BatteryTracker.fold(BatteryTracker.start(first), first, second)

        assertEquals(1f, session.idleDrainPerHour!!, 0.01f)
        assertNull(session.activeDrainPerHour)
    }

    @Test
    fun `the state at the start of an interval owns it`() {
        // Screen was on when the interval began, so the whole interval is active
        // even though the sample arrives with the screen off.
        val first = sample(0, level = 100, screenOn = true)
        val second = sample(HOUR, level = 95, screenOn = false)

        val session = BatteryTracker.fold(BatteryTracker.start(first), first, second)

        assertEquals(HOUR, session.screenOnMillis)
        assertEquals(0L, session.screenOffMillis)
    }

    @Test
    fun `deep sleep is only counted while the screen is off`() {
        // One hour elapsed, fifteen minutes of it awake.
        val first = sample(0, awake = 0, screenOn = false)
        val second = sample(HOUR, awake = 900_000, screenOn = false)

        val session = BatteryTracker.fold(BatteryTracker.start(first), first, second)

        assertEquals(900_000L, session.screenOffAwakeMillis)
        assertEquals(HOUR - 900_000L, session.screenOffDeepSleepMillis)
        assertEquals(0.75f, session.screenOffDeepSleepFraction, 0.01f)
    }

    @Test
    fun `a device held fully awake reports no deep sleep`() {
        val first = sample(0, awake = 0, screenOn = false)
        val second = sample(HOUR, awake = HOUR, screenOn = false)

        val session = BatteryTracker.fold(BatteryTracker.start(first), first, second)

        assertEquals(0L, session.screenOffDeepSleepMillis)
        assertEquals(0f, session.screenOffDeepSleepFraction, 0.001f)
    }

    @Test
    fun `unplugging starts a new session`() {
        val charging = sample(0, level = 80, charging = true)
        var session = BatteryTracker.start(charging)
        session = BatteryTracker.fold(session, charging, sample(HOUR, level = 100, charging = true))

        val unplugged = sample(2 * HOUR, level = 100, charging = false)
        session = BatteryTracker.fold(session, sample(HOUR, level = 100, charging = true), unplugged)

        assertEquals(2 * HOUR, session.startedAtElapsedMillis)
        assertEquals(0L, session.screenOnMillis)
        assertEquals(0L, session.screenOffMillis)
    }

    @Test
    fun `a reboot restarts rather than reporting a negative interval`() {
        val before = sample(10 * HOUR, level = 50)
        var session = BatteryTracker.start(before)
        // elapsedRealtime resets to near zero after a reboot.
        session = BatteryTracker.fold(session, before, sample(1_000, level = 50))

        assertEquals(1_000L, session.startedAtElapsedMillis)
        assertEquals(0L, session.screenOffMillis)
    }

    @Test
    fun `charging never contributes to a drain rate`() {
        val first = sample(0, level = 50, charging = true)
        val session = BatteryTracker.fold(
            BatteryTracker.start(first),
            first,
            sample(HOUR, level = 70, charging = true),
        )
        assertNull(session.activeDrainPerHour)
        assertNull(session.idleDrainPerHour)
    }

    @Test
    fun `a charge counter that rises is not counted as negative drain`() {
        val first = sample(0, level = 90, charge = 3_000_000, screenOn = true)
        val session = BatteryTracker.fold(
            BatteryTracker.start(first),
            first,
            sample(HOUR, level = 90, charge = 3_100_000, screenOn = true),
        )
        assertEquals(0L, session.screenOnDrainedMicroAmpHours)
    }

    @Test
    fun `mAh drained accumulates from the charge counter`() {
        val first = sample(0, level = 90, charge = 3_000_000, screenOn = true)
        val session = BatteryTracker.fold(
            BatteryTracker.start(first),
            first,
            sample(HOUR, level = 80, charge = 2_600_000, screenOn = true),
        )
        assertEquals(400_000L, session.screenOnDrainedMicroAmpHours)
    }

    @Test
    fun `a window too short to be meaningful yields no rate`() {
        // A one percent step over ten seconds extrapolates to 360%/h.
        val first = sample(0, level = 100, screenOn = true)
        val session = BatteryTracker.fold(
            BatteryTracker.start(first),
            first,
            sample(10_000, level = 99, screenOn = true),
        )
        assertNull(session.activeDrainPerHour)
    }

    @Test
    fun `watts come from current and voltage`() {
        val first = sample(0, current = -956_000, voltage = 3766, screenOn = true)
        val session = BatteryTracker.start(first)
        // 0.956 A × 3.766 V ≈ 3.6 W
        assertEquals(3.6f, session.watts!!, 0.05f)
    }

    @Test
    fun `time remaining follows the rate of the current screen state`() {
        val first = sample(0, level = 50, screenOn = true)
        val session = BatteryTracker.fold(
            BatteryTracker.start(first),
            first,
            sample(HOUR, level = 40, screenOn = true),
        )
        // 40% left at 10%/h is four hours.
        val remaining = session.estimatedMillisRemaining!!
        assertTrue("got ${remaining / HOUR}h", remaining in (3.5 * HOUR).toLong()..(4.5 * HOUR).toLong())
    }
}
