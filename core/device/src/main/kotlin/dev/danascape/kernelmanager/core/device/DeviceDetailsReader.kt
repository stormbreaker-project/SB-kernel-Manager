// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import android.app.ActivityManager
import android.content.Context
import android.media.MediaCodecList
import android.os.Build
import android.system.Os
import android.system.OsConstants
import dev.danascape.kernelmanager.core.model.BuildDetails
import dev.danascape.kernelmanager.core.model.CodecSummary
import dev.danascape.kernelmanager.core.model.DeviceDetails
import dev.danascape.kernelmanager.core.model.MemoryDetails
import java.io.File

private const val NOTABLE_CODEC_LIMIT = 8
private const val BYTES_PER_KILOBYTE = 1024

private val NOTABLE_MIME_TYPES =
    listOf(
        "video/av01" to "AV1",
        "video/hevc" to "HEVC",
        "video/x-vnd.on2.vp9" to "VP9",
        "video/avc" to "H.264",
        "video/dolby-vision" to "Dolby Vision",
        "audio/opus" to "Opus",
        "audio/flac" to "FLAC",
        "audio/eac3-joc" to "Atmos",
    )

/** The parts of the device screen that need a Context rather than a sysfs read. */
class DeviceDetailsReader(
    private val context: Context,
) {
    private val display = DisplayInfoReader(context)
    private val sensors = SensorInfoReader(context)
    private val cameras = CameraInfoReader(context)

    fun read(properties: SystemProperties): DeviceDetails =
        DeviceDetails(
            build = readBuild(properties),
            display = display.read(),
            memory = readMemory(),
            sensors = sensors.read(),
            cameras = cameras.read(),
            codecs = readCodecs(),
        )

    private fun readBuild(properties: SystemProperties): BuildDetails =
        BuildDetails(
            brand = Build.BRAND.takeIf { it.isNotBlank() },
            product = Build.PRODUCT.takeIf { it.isNotBlank() },
            board = Build.BOARD.takeIf { it.isNotBlank() },
            device = Build.DEVICE.takeIf { it.isNotBlank() },
            hardware = Build.HARDWARE.takeIf { it.isNotBlank() },
            bootloader = Build.BOOTLOADER.takeIf { it.isNotBlank() },
            radio = properties["gsm.version.baseband"] ?: Build.getRadioVersion()?.takeIf { it.isNotBlank() },
            incremental = Build.VERSION.INCREMENTAL.takeIf { it.isNotBlank() },
            host = Build.HOST.takeIf { it.isNotBlank() },
            buildTimeMillis = Build.TIME,
            javaVm = System.getProperty("java.vm.version"),
            kernelFull = runCatching { File("/proc/version").readText().trim() }.getOrNull(),
        )

    private fun readMemory(): MemoryDetails? {
        val manager = context.getSystemService(ActivityManager::class.java) ?: return null
        val info = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(info)
        val meminfo = readMeminfo()
        return MemoryDetails(
            totalBytes = info.totalMem,
            availableBytes = info.availMem,
            lowMemory = info.lowMemory,
            thresholdBytes = info.threshold,
            swapTotalBytes = meminfo["SwapTotal"],
            pageSizeBytes = runCatching { Os.sysconf(OsConstants._SC_PAGESIZE) }.getOrNull(),
        )
    }

    private fun readMeminfo(): Map<String, Long> =
        runCatching {
            File("/proc/meminfo")
                .readLines()
                .mapNotNull { line ->
                    val parts = line.split(':', limit = 2)
                    if (parts.size != 2) return@mapNotNull null
                    val kb = parts[1].trim().removeSuffix(" kB").toLongOrNull() ?: return@mapNotNull null
                    parts[0] to kb * BYTES_PER_KILOBYTE
                }.toMap()
        }.getOrDefault(emptyMap())

    private fun readCodecs(): CodecSummary? =
        runCatching {
            val infos = MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos
            val supported = infos.flatMap { it.supportedTypes.toList() }.map { it.lowercase() }.toSet()
            CodecSummary(
                decoders = infos.count { !it.isEncoder },
                encoders = infos.count { it.isEncoder },
                hardwareAccelerated = infos.count { it.isHardwareAccelerated },
                notableFormats =
                    NOTABLE_MIME_TYPES
                        .filter { (mime, _) -> mime in supported }
                        .map { (_, label) -> label }
                        .take(NOTABLE_CODEC_LIMIT),
            )
        }.getOrNull()
}
