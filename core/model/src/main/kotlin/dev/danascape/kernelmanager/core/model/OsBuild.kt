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
    /** `release-keys` on a signed stock build, `test-keys` on most custom ones. */
    val tags: String?,
    /** `user`, `userdebug` or `eng`. */
    val type: String?,
    /** Null when this looks like a stock build. */
    val rom: CustomRom?,
) {
    val isStockSigned: Boolean get() = tags == "release-keys" && rom == null
}

data class CustomRom(
    val name: String,
    val version: String,
)

/**
 * Whether the device can take a custom kernel at all.
 *
 * Normally the first question anyone asks before flashing, and normally
 * answered only by rebooting to fastboot.
 */
data class BootState(
    val bootloaderUnlocked: Boolean?,
    /** `green`, `yellow`, `orange` or `red` — dm-verity's view of the boot chain. */
    val verifiedBootState: String?,
    /** `file` for FBE, `block` for full-disk. */
    val encryption: String?,
) {
    val canFlash: Boolean? get() = bootloaderUnlocked
}

/** The silicon. */
data class SocInfo(
    /** `ro.board.platform`, e.g. `zumapro`, `lito`. */
    val platform: String?,
    val hardware: String?,
    val manufacturer: String?,
    val model: String?,
    val supportedAbis: List<String>,
)
