package dev.danascape.kernelmanager.core.model

/** What the app can tell about the device without root or a permission. */
data class DeviceIdentity(
    /** `ro.product.device`. Alias resolution (surya/karna) happens against the manifest. */
    val codename: String,
    val model: String,
    val manufacturer: String,
    val androidRelease: String,
    val sdkInt: Int,
    /** `uname -r`. */
    val kernelRelease: String,
    /**
     * Our kernels set CONFIG_LOCALVERSION="-StormBreaker", so the tag says which
     * family is running — not which build. Every release reports the same
     * string, which is why the update check cannot be based on it.
     */
    val isStormBreakerKernel: Boolean,
) {
    val displayName: String get() = "$manufacturer $model"
}
