package dev.danascape.kernelmanager.core.device

import android.os.Build
import dev.danascape.kernelmanager.core.model.DeviceIdentity

private const val LOCALVERSION_TAG = "stormbreaker"
private const val UNKNOWN = "unknown"

object DeviceIdentityReader {

    fun read(): DeviceIdentity {
        val kernel = kernelRelease()
        return DeviceIdentity(
            codename = Build.DEVICE.orEmpty().ifBlank { UNKNOWN },
            model = Build.MODEL.orEmpty().ifBlank { UNKNOWN },
            manufacturer = Build.MANUFACTURER.orEmpty().ifBlank { UNKNOWN },
            androidRelease = Build.VERSION.RELEASE.orEmpty().ifBlank { UNKNOWN },
            sdkInt = Build.VERSION.SDK_INT,
            kernelRelease = kernel,
            isStormBreakerKernel = kernel.contains(LOCALVERSION_TAG, ignoreCase = true),
        )
    }

    /**
     * `os.version` is uname's release field.
     *
     * There is no /proc/version fallback: it is denied to unprivileged apps on
     * modern Android, so reading it would only ever produce a permission error.
     */
    private fun kernelRelease(): String =
        System.getProperty("os.version")?.takeIf { it.isNotBlank() } ?: UNKNOWN
}
