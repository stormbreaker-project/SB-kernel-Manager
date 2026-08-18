package dev.danascape.kernelmanager.core.model

/**
 * Everything about the device that does not change while the process is alive.
 *
 * Read once. Kept apart from [Vitals], which is sampled repeatedly.
 */
data class DeviceProfile(
    val identity: DeviceIdentity,
    val os: OsBuild,
    val boot: BootState,
    val soc: SocInfo,
    val cpu: CpuTopology?,
    /** A su binary exists. Not a guarantee this app has been granted root. */
    val suBinaryPresent: Boolean,
)
