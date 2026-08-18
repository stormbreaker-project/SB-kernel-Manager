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
 * Reads what a stock device will actually give an unprivileged app.
 *
 * Memory and battery come from framework APIs rather than /proc, because
 * /proc/stat, /proc/loadavg and /proc/version are all denied.
 */
class SystemVitalsReader(context: Context) {

    private val appContext = context.applicationContext

    fun read(): Vitals = Vitals(
        cpu = CpuReader.read(),
        memory = readMemory(),
        battery = readBattery(),
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
        )
    }
}
