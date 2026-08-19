// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import dev.danascape.kernelmanager.core.model.CameraInfo

/** Camera2 characteristics, which need no permission until a camera is opened. */
class CameraInfoReader(
    private val context: Context,
) {
    fun read(): List<CameraInfo> {
        val manager = context.getSystemService(CameraManager::class.java) ?: return emptyList()
        return runCatching { manager.cameraIdList }
            .getOrDefault(emptyArray())
            .mapNotNull { id -> runCatching { describe(manager, id) }.getOrNull() }
    }

    private fun describe(
        manager: CameraManager,
        id: String,
    ): CameraInfo {
        val characteristics = manager.getCameraCharacteristics(id)
        val pixelArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
        val physical = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
        val iso = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)

        return CameraInfo(
            id = id,
            facing = facingName(characteristics.get(CameraCharacteristics.LENS_FACING)),
            widthPx = pixelArray?.width ?: 0,
            heightPx = pixelArray?.height ?: 0,
            apertures =
                characteristics
                    .get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)
                    ?.toList()
                    .orEmpty(),
            focalLengths =
                characteristics
                    .get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                    ?.toList()
                    .orEmpty(),
            sensorWidthMm = physical?.width,
            sensorHeightMm = physical?.height,
            isoMin = iso?.lower,
            isoMax = iso?.upper,
            hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true,
            hardwareLevel = levelName(characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)),
            maxDigitalZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM),
        )
    }

    private fun facingName(facing: Int?): String =
        when (facing) {
            CameraCharacteristics.LENS_FACING_FRONT -> "Front"
            CameraCharacteristics.LENS_FACING_BACK -> "Back"
            CameraCharacteristics.LENS_FACING_EXTERNAL -> "External"
            else -> "Unknown"
        }

    private fun levelName(level: Int?): String? =
        when (level) {
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "Legacy"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "Limited"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "Full"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "Level 3"
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL -> "External"
            else -> null
        }
}
