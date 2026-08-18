package dev.danascape.kernelmanager.core.model

/**
 * Thermal throttling severity.
 *
 * Not a temperature. Exact degrees are denied to unprivileged apps, but the
 * framework does report how hard the device is being throttled, which is the
 * part a user can act on.
 */
enum class ThermalStatus {
    NONE,
    LIGHT,
    MODERATE,
    SEVERE,
    CRITICAL,
    EMERGENCY,
    SHUTDOWN,
    UNKNOWN,
}

data class StorageVitals(
    val usedBytes: Long,
    val totalBytes: Long,
    /** `f2fs` or `ext4`, from /proc/mounts. */
    val fileSystem: String?,
) {
    val usedFraction: Float get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f
}

data class NetworkVitals(
    val rxBytes: Long,
    val txBytes: Long,
)
