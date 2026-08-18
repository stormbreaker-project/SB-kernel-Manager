// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/** What the device is running. */
data class OsBuild(
    val androidRelease: String,
    val sdkInt: Int,
    val securityPatch: String?,
    val buildId: String?,
    val fingerprint: String?,
    val tags: String?,
    val type: String?,
    val rom: CustomRom?,
) {
    val isStockSigned: Boolean get() = tags == "release-keys" && rom == null
}

data class CustomRom(
    val name: String,
    val version: String,
)

/** Whether the device can take a custom kernel at all. */
data class BootState(
    val bootloaderUnlocked: Boolean?,
    val verifiedBootState: String?,
    val encryption: String?,
) {
    val canFlash: Boolean? get() = bootloaderUnlocked
}

/** The silicon. */
data class SocInfo(
    val platform: String?,
    val hardware: String?,
    val manufacturer: String?,
    val model: String?,
    val supportedAbis: List<String>,
)
