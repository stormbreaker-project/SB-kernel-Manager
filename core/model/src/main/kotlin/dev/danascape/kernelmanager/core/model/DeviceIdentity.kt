// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/** What the app can tell about the device without root or a permission. */
data class DeviceIdentity(
    val codename: String,
    val model: String,
    val manufacturer: String,
    val androidRelease: String,
    val sdkInt: Int,
    val kernelRelease: String,
    val isStormBreakerKernel: Boolean,
) {
    val displayName: String get() = "$manufacturer $model"
}
