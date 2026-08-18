// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/** How the CPU is laid out and what it is allowed to do. */
data class CpuTopology(
    val clusters: List<CpuCluster>,
    val coreCount: Int,
    val features: List<String>,
) {
    val isHeterogeneous: Boolean get() = clusters.size > 1
}

/** One cpufreq policy — a set of cores that scale together. */
data class CpuCluster(
    val id: Int,
    val cores: List<Int>,
    val minKhz: Int?,
    val maxKhz: Int?,
    val hardwareMaxKhz: Int?,
    val availableKhz: List<Int>,
    val governor: String?,
    val availableGovernors: List<String>,
    val partId: String?,
    val implementer: String?,
)

/** Utilisation, derived from idle residency rather than the denied /proc/stat. */
data class CpuLoad(
    val perCore: List<Float>,
) {
    val average: Float get() = if (perCore.isEmpty()) 0f else perCore.average().toFloat()
}

/** A point-in-time snapshot used to derive [CpuLoad] from two samples. */
data class CpuIdleSample(
    val elapsedNanos: Long,
    val idleMicrosPerCore: List<Long>,
)
