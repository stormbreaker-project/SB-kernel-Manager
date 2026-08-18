// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.core.content.getSystemService
import dev.danascape.kernelmanager.core.model.BatteryVitals
import dev.danascape.kernelmanager.core.model.MemoryVitals
import dev.danascape.kernelmanager.core.model.Vitals

/**
 * Samples live state.
 *
 * Memory and battery come from framework APIs rather than /proc and
 * /sys/class/power_supply, both of which are denied.
 */
class SystemVitalsReader(context: Context) {

    private val appContext = context.applicationContext
    private val platform = PlatformVitalsReader(appContext)

    /**
     * @param load supplied by the caller, since utilisation needs two samples
     *   spaced in time and this call is a single point.
     */
    fun read(load: dev.danascape.kernelmanager.core.model.CpuLoad? = null): Vitals = Vitals(
        cpu = CpuReader.read(),
        load = load,
        memory = readMemory(),
        battery = readBattery(),
        storage = platform.storage(),
        network = platform.network(),
        thermal = platform.thermalStatus(),
        uptimeMillis = platform.uptimeMillis(),
        sleep = platform.sleepStats(),
    )

    private fun readMemory(): MemoryVitals? {
        val manager = appContext.getSystemService<ActivityManager>() ?: return null
        val info = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(info)
        if (info.totalMem <= 0) return null
        return MemoryVitals(
            usedBytes = (info.totalMem - info.availMem).coerceAtLeast(0),
            totalBytes = info.totalMem,
        )
    }

    /** The battery broadcast is sticky, so a null receiver returns it immediately. */
    private fun readBattery(): BatteryVitals? {
        val intent = appContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
        ) ?: return null

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val tenthsC = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)

        return BatteryVitals(
            percent = (level * 100) / scale,
            temperatureC = tenthsC.takeIf { it != Int.MIN_VALUE }?.let { it / 10f },
            charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL,
            currentMicroAmps = readCurrentMicroAmps(),
            chargeCounterMicroAmpHours = platform.chargeCounterMicroAmpHours(),
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1).toHealthName(),
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
        )
    }

    /** /sys/class/power_supply is denied, so this is the only route to draw. */
    private fun readCurrentMicroAmps(): Int? {
        val manager = appContext.getSystemService<BatteryManager>() ?: return null
        return manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
            .takeIf { it != Int.MIN_VALUE && it != 0 }
    }

    private fun Int.toHealthName(): String? = when (this) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Failing"
        else -> null
    }
}
