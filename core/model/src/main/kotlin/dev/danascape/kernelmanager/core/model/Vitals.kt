package dev.danascape.kernelmanager.core.model

/**
 * Live system state.
 *
 * Every field is independently nullable: Android 12+ denies an unprivileged app
 * most of these sources, and which ones differ per device. Null means "no
 * readable source here", not "not loaded yet".
 */
data class Vitals(
    val cpu: CpuVitals?,
    val load: CpuLoad?,
    val memory: MemoryVitals?,
    val battery: BatteryVitals?,
    val storage: StorageVitals?,
    val network: NetworkVitals?,
    val thermal: ThermalStatus,
    val uptimeMillis: Long,
)

data class CpuVitals(
    /** Current frequency per core, in kHz. Empty when cpufreq is not readable. */
    val perCoreKhz: List<Int>,
    val maxKhz: Int?,
    val governor: String?,
    /**
     * Normally null: thermal zones are denied to unprivileged apps, which is
     * the restriction a StormBreaker kernel node is meant to lift.
     */
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
    /** Instantaneous draw in microamps; negative while discharging on most devices. */
    val currentMicroAmps: Int?,
    val health: String?,
    val technology: String?,
)
