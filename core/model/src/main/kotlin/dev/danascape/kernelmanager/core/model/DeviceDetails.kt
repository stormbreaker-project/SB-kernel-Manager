// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/** Everything the device screen shows that the profile does not already carry. */
data class DeviceDetails(
    val build: BuildDetails,
    val display: DisplayInfo?,
    val memory: MemoryDetails?,
    val sensors: List<SensorInfo>,
    val cameras: List<CameraInfo>,
    val codecs: CodecSummary?,
)

/** Build fields beyond the ones the Discover card needs. */
data class BuildDetails(
    val brand: String?,
    val product: String?,
    val board: String?,
    val device: String?,
    val hardware: String?,
    val bootloader: String?,
    val radio: String?,
    val incremental: String?,
    val host: String?,
    val buildTimeMillis: Long,
    val javaVm: String?,
    val kernelFull: String?,
)

data class DisplayInfo(
    val widthPx: Int,
    val heightPx: Int,
    val densityDpi: Int,
    val xDpi: Float,
    val yDpi: Float,
    val refreshHz: Float,
    val supportedRefreshHz: List<Float>,
    val hdrTypes: List<String>,
    val diagonalInches: Float?,
) {
    val aspectRatio: String
        get() {
            val divisor = gcd(widthPx, heightPx).coerceAtLeast(1)
            return "${heightPx / divisor}:${widthPx / divisor}"
        }

    private fun gcd(
        a: Int,
        b: Int,
    ): Int = if (b == 0) a else gcd(b, a % b)
}

data class MemoryDetails(
    val totalBytes: Long,
    val availableBytes: Long,
    val lowMemory: Boolean,
    val thresholdBytes: Long,
    val swapTotalBytes: Long?,
    val pageSizeBytes: Long?,
)

data class SensorInfo(
    val name: String,
    val vendor: String,
    val type: String,
    val power: Float,
    val resolution: Float,
    val maxRange: Float,
    val isWakeUp: Boolean,
)

data class CameraInfo(
    val id: String,
    val facing: String,
    val widthPx: Int,
    val heightPx: Int,
    val apertures: List<Float>,
    val focalLengths: List<Float>,
    val sensorWidthMm: Float?,
    val sensorHeightMm: Float?,
    val isoMin: Int?,
    val isoMax: Int?,
    val hasFlash: Boolean,
    val hardwareLevel: String?,
    val maxDigitalZoom: Float?,
) {
    val megapixels: Float get() = widthPx.toFloat() * heightPx / 1_000_000f

    /** Sensor pitch in micrometres, from the die size over the pixel count. */
    val pixelSizeMicrons: Float?
        get() = sensorWidthMm?.takeIf { widthPx > 0 }?.let { it * 1000f / widthPx }
}

data class CodecSummary(
    val decoders: Int,
    val encoders: Int,
    val hardwareAccelerated: Int,
    val notableFormats: List<String>,
)
