// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/** Live system state. */
data class Vitals(
    val cpu: CpuVitals?,
    val load: CpuLoad?,
    val memory: MemoryVitals?,
    val battery: BatteryVitals?,
    val storage: StorageVitals?,
    val network: NetworkVitals?,
    val thermal: ThermalStatus,
    val uptimeMillis: Long,
    val sleep: SleepStats,
)

data class CpuVitals(
    val perCoreKhz: List<Int>,
    val maxKhz: Int?,
    val governor: String?,
    val temperatureC: Float?,
)

data class MemoryVitals(
    val usedBytes: Long,
    val totalBytes: Long,
) {
    val usedFraction: Float get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f
}

data class BatteryVitals(
    val percent: Int,
    val temperatureC: Float?,
    val charging: Boolean,
    val currentMicroAmps: Int?,
    val chargeCounterMicroAmpHours: Int?,
    val health: String?,
    val technology: String?,
)
