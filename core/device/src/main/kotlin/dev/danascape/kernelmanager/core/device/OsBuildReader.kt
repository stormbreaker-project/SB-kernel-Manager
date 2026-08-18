// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import android.os.Build
import dev.danascape.kernelmanager.core.model.BootState
import dev.danascape.kernelmanager.core.model.CustomRom
import dev.danascape.kernelmanager.core.model.OsBuild
import dev.danascape.kernelmanager.core.model.SocInfo

private val KNOWN_ROMS =
    listOf(
        "ro.lineage.version" to "LineageOS",
        "ro.crdroid.version" to "crDroid",
        "ro.evolution.version" to "Evolution X",
        "ro.aospa.version" to "Paranoid Android",
        "ro.pa.version" to "Paranoid Android",
        "ro.rr.version" to "Resurrection Remix",
        "ro.havoc.version" to "Havoc-OS",
        "ro.derp.version" to "DerpFest",
        "ro.omni.version" to "OmniROM",
        "ro.potato.version" to "PotatoOS",
        "ro.pixelexperience.version" to "Pixel Experience",
        "ro.modversion" to "Custom ROM",
    )

class OsBuildReader(
    private val properties: SystemProperties,
) {
    fun read(): OsBuild =
        OsBuild(
            androidRelease = Build.VERSION.RELEASE.orEmpty(),
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = Build.VERSION.SECURITY_PATCH.takeIf { it.isNotBlank() },
            buildId = Build.ID.takeIf { it.isNotBlank() },
            fingerprint = Build.FINGERPRINT.takeIf { it.isNotBlank() },
            tags = Build.TAGS.takeIf { it.isNotBlank() },
            type = Build.TYPE.takeIf { it.isNotBlank() },
            rom = detectRom(),
        )

    private fun detectRom(): CustomRom? =
        KNOWN_ROMS.firstNotNullOfOrNull { (key, name) ->
            properties[key]?.let { CustomRom(name, it) }
        }

    fun readBootState(): BootState =
        BootState(
            bootloaderUnlocked = properties["ro.boot.flash.locked"]?.let { it == "0" },
            verifiedBootState = properties["ro.boot.verifiedbootstate"],
            encryption = properties["ro.crypto.type"],
        )

    fun readSoc(): SocInfo =
        SocInfo(
            platform = properties["ro.board.platform"],
            hardware = properties["ro.hardware"] ?: Build.HARDWARE.takeIf { it.isNotBlank() },
            manufacturer =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Build.SOC_MANUFACTURER.takeIf { it.isNotBlank() && it != Build.UNKNOWN }
                } else {
                    null
                },
            model =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Build.SOC_MODEL.takeIf { it.isNotBlank() && it != Build.UNKNOWN }
                } else {
                    null
                },
            supportedAbis = Build.SUPPORTED_ABIS.orEmpty().toList(),
        )
}
