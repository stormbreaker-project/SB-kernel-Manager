package dev.danascape.kernelmanager.core.device

import dev.danascape.kernelmanager.core.model.CpuVitals
import java.io.File

private const val CPU_ROOT = "/sys/devices/system/cpu"
private const val THERMAL_ROOT = "/sys/class/thermal"

/**
 * cpufreq is one of the few kernel nodes still world-readable on a stock
 * device. Thermal zones are not, so a temperature here is the exception.
 */
object CpuReader {

    fun read(): CpuVitals? {
        val cores = coreDirectories()
        if (cores.isEmpty()) return null

        val frequencies = cores.mapNotNull { core ->
            core.resolve("cpufreq/scaling_cur_freq").readIntOrNull()
        }
        if (frequencies.isEmpty()) return null

        return CpuVitals(
            perCoreKhz = frequencies,
            // Across every core, not core 0. On a big.LITTLE SoC core 0 is a
            // little core, and its ceiling is well below what a big core
            // reports as its current frequency.
            maxKhz = cores.mapNotNull { it.resolve("cpufreq/cpuinfo_max_freq").readIntOrNull() }
                .maxOrNull(),
            governor = cores.first().resolve("cpufreq/scaling_governor").readTextOrNull(),
            temperatureC = cpuTemperatureC(),
        )
    }

    private fun coreDirectories(): List<File> =
        File(CPU_ROOT).listFiles { file -> file.name.matches(Regex("cpu\\d+")) }
            ?.sortedBy { it.name.removePrefix("cpu").toIntOrNull() ?: 0 }
            .orEmpty()

    /**
     * Normally null. Every thermal zone is SELinux-denied to an unprivileged
     * app on Android 12+, which is the restriction a StormBreaker kernel node
     * is meant to lift.
     */
    private fun cpuTemperatureC(): Float? {
        val zones = File(THERMAL_ROOT).listFiles { file -> file.name.startsWith("thermal_zone") }
            ?: return null
        for (zone in zones) {
            val type = zone.resolve("type").readTextOrNull() ?: continue
            if (!type.contains("cpu", ignoreCase = true)) continue
            val milliC = zone.resolve("temp").readIntOrNull() ?: continue
            return milliC / 1000f
        }
        return null
    }
}

internal fun File.readTextOrNull(): String? = try {
    takeIf { it.canRead() }?.readText()?.trim()?.takeIf { it.isNotEmpty() }
} catch (_: Exception) {
    null
}

internal fun File.readIntOrNull(): Int? = readTextOrNull()?.toIntOrNull()
