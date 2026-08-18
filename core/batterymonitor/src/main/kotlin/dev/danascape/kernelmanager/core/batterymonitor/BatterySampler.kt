// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.batterymonitor

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.content.getSystemService
import dev.danascape.kernelmanager.core.battery.BatterySample

private const val TENTHS_PER_DEGREE = 10f

/** Takes one reading of the battery and both clocks. */
class BatterySampler(
    context: Context,
) {
    private val appContext = context.applicationContext

    fun sample(): BatterySample? {
        val intent =
            appContext.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            ) ?: return null

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null

        val manager = appContext.getSystemService<BatteryManager>()
        val power = appContext.getSystemService<PowerManager>()
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val tenthsC = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        val milliVolts = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)

        return BatterySample(
            elapsedMillis = SystemClock.elapsedRealtime(),
            awakeMillis = SystemClock.uptimeMillis(),
            levelPercent = (level * 100) / scale,
            chargeMicroAmpHours =
                manager
                    ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
                    ?.takeIf { it != Int.MIN_VALUE && it > 0 },
            currentMicroAmps =
                manager
                    ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                    ?.takeIf { it != Int.MIN_VALUE && it != 0 },
            voltageMilliVolts = milliVolts.takeIf { it > 0 },
            temperatureC = tenthsC.takeIf { it != Int.MIN_VALUE }?.let { it / TENTHS_PER_DEGREE },
            charging =
                status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL,
            screenOn = power?.isInteractive ?: true,
        )
    }
}
