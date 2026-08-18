// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import dev.danascape.kernelmanager.core.model.CpuCluster
import dev.danascape.kernelmanager.core.model.CpuTopology
import java.io.File

private const val CPU_ROOT = "/sys/devices/system/cpu"
private const val CPUINFO = "/proc/cpuinfo"

/**
 * Reads the cpufreq policy groups.
 *
 * Enumeration works here but not under /sys/class, where listing is denied even
 * though individual files read fine.
 */
object CpuTopologyReader {

    fun read(): CpuTopology? {
        val policies = File("$CPU_ROOT/cpufreq").listFiles { file ->
            file.name.startsWith("policy")
        }?.sortedBy { it.name.removePrefix("policy").toIntOrNull() ?: 0 }

        val coreIds = File(CPU_ROOT).listFiles { file -> file.name.matches(Regex("cpu\\d+")) }
            ?.mapNotNull { it.name.removePrefix("cpu").toIntOrNull() }
            ?.sorted()
            .orEmpty()

        if (policies.isNullOrEmpty()) return null

        val parts = readCoreParts()
        val clusters = policies.mapNotNull { policy ->
            val id = policy.name.removePrefix("policy").toIntOrNull() ?: return@mapNotNull null
            val cores = policy.resolve("related_cpus").readTextOrNull()
                ?.split(' ')?.mapNotNull(String::toIntOrNull)
                ?: listOf(id)
            CpuCluster(
                id = id,
                cores = cores,
                minKhz = policy.resolve("scaling_min_freq").readIntOrNull(),
                maxKhz = policy.resolve("scaling_max_freq").readIntOrNull(),
                hardwareMaxKhz = policy.resolve("cpuinfo_max_freq").readIntOrNull(),
                availableKhz = policy.resolve("scaling_available_frequencies").readTextOrNull()
                    ?.split(' ')?.mapNotNull(String::toIntOrNull).orEmpty(),
                governor = policy.resolve("scaling_governor").readTextOrNull(),
                availableGovernors = policy.resolve("scaling_available_governors").readTextOrNull()
                    ?.split(' ')?.filter { it.isNotBlank() }.orEmpty(),
                partId = parts[cores.firstOrNull()]?.first,
                implementer = parts[cores.firstOrNull()]?.second,
            )
        }

        return CpuTopology(
            clusters = clusters,
            coreCount = coreIds.size.takeIf { it > 0 } ?: clusters.sumOf { it.cores.size },
            features = readFeatures(),
        )
    }

    /** Maps core id to (part, implementer). /proc/cpuinfo lists them per processor. */
    private fun readCoreParts(): Map<Int, Pair<String?, String?>> {
        val text = File(CPUINFO).readTextOrNull() ?: return emptyMap()
        val result = mutableMapOf<Int, Pair<String?, String?>>()
        var core: Int? = null
        var part: String? = null
        var implementer: String? = null

        text.lineSequence().forEach { line ->
            val (key, value) = line.split(':', limit = 2).map(String::trim).let {
                if (it.size == 2) it[0] to it[1] else return@forEach
            }
            when (key) {
                "processor" -> {
                    core?.let { result[it] = part to implementer }
                    core = value.toIntOrNull()
                    part = null
                    implementer = null
                }
                "CPU part" -> part = value
                "CPU implementer" -> implementer = value
            }
        }
        core?.let { result[it] = part to implementer }
        return result
    }

    private fun readFeatures(): List<String> = File(CPUINFO).readTextOrNull()
        ?.lineSequence()
        ?.firstOrNull { it.startsWith("Features") }
        ?.substringAfter(':')
        ?.trim()
        ?.split(' ')
        ?.filter { it.isNotBlank() }
        .orEmpty()
}
