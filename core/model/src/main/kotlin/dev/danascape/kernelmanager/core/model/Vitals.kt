package dev.danascape.kernelmanager.core.model

/**
 * A glanceable snapshot of system state.
 *
 * Each field is independently nullable because Android 12+ SELinux denies an
 * unprivileged app most of these nodes, and which ones differ per device. A
 * null field means "no readable source here", not "not loaded yet".
 */
data class Vitals(
    val cpu: CpuVitals?,
    val memory: MemoryVitals?,
    val battery: BatteryVitals?,
) {
    /**
     * True when nothing at all could be read — the case a StormBreaker kernel
     * exposing its own node is meant to fix.
     */
    val isEmpty: Boolean get() = cpu == null && memory == null && battery == null
}

data class CpuVitals(
    /** Current frequency per core, in kHz. Empty when cpufreq is not readable. */
    val perCoreKhz: List<Int>,
    val maxKhz: Int?,
    val governor: String?,
    /**
     * Thermal zones are denied to unprivileged apps on most modern devices, so
     * this is normally false until the kernel exposes a readable node.
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
)
