// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import dev.danascape.kernelmanager.core.model.CpuVitals
import java.io.File

private const val CPU_ROOT = "/sys/devices/system/cpu"
private const val THERMAL_ROOT = "/sys/class/thermal"

private const val MILLI_DEGREES_PER_DEGREE = 1000f

/** cpufreq is one of the few kernel nodes still world-readable on a stock device. */
object CpuReader {
    fun read(): CpuVitals? {
        val cores = coreDirectories()
        if (cores.isEmpty()) return null

        val frequencies =
            cores.mapNotNull { core ->
                core.resolve("cpufreq/scaling_cur_freq").readIntOrNull()
            }
        if (frequencies.isEmpty()) return null

        return CpuVitals(
            perCoreKhz = frequencies,
            maxKhz =
                cores
                    .mapNotNull { it.resolve("cpufreq/cpuinfo_max_freq").readIntOrNull() }
                    .maxOrNull(),
            governor = cores.first().resolve("cpufreq/scaling_governor").readTextOrNull(),
            temperatureC = cpuTemperatureC(),
        )
    }

    private fun coreDirectories(): List<File> =
        File(CPU_ROOT)
            .listFiles { file -> file.name.matches(Regex("cpu\\d+")) }
            ?.sortedBy { it.name.removePrefix("cpu").toIntOrNull() ?: 0 }
            .orEmpty()

    /** Normally null. */
    private fun cpuTemperatureC(): Float? {
        val zones =
            File(THERMAL_ROOT).listFiles { file -> file.name.startsWith("thermal_zone") }
                ?: return null
        return zones
            .firstNotNullOfOrNull { zone ->
                zone
                    .resolve("type")
                    .readTextOrNull()
                    ?.takeIf { it.contains("cpu", ignoreCase = true) }
                    ?.let { zone.resolve("temp").readIntOrNull() }
            }?.div(MILLI_DEGREES_PER_DEGREE)
    }
}

internal fun File.readTextOrNull(): String? =
    try {
        takeIf { it.canRead() }?.readText()?.trim()?.takeIf { it.isNotEmpty() }
    } catch (_: Exception) {
        null
    }

internal fun File.readIntOrNull(): Int? = readTextOrNull()?.toIntOrNull()
