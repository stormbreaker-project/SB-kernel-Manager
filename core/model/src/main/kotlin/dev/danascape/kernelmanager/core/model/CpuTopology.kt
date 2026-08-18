package dev.danascape.kernelmanager.core.model

/** How the CPU is laid out and what it is allowed to do. */
data class CpuTopology(
    val clusters: List<CpuCluster>,
    val coreCount: Int,
    /** ISA extensions from /proc/cpuinfo — `sve2`, `i8mm`, `bf16` and so on. */
    val features: List<String>,
) {
    val isHeterogeneous: Boolean get() = clusters.size > 1
}

/**
 * One cpufreq policy — a set of cores that scale together.
 *
 * On big.LITTLE this is the unit that matters: limits and governor are
 * per-policy, so reading them from core 0 describes only the little cluster.
 */
data class CpuCluster(
    /** Policy id, which is the first core in the group. */
    val id: Int,
    val cores: List<Int>,
    val minKhz: Int?,
    val maxKhz: Int?,
    val hardwareMaxKhz: Int?,
    /** The OPP table: every frequency this cluster can be set to. */
    val availableKhz: List<Int>,
    val governor: String?,
    val availableGovernors: List<String>,
    /** ARM part number, e.g. `0xd80` for a Cortex-A520. */
    val partId: String?,
    val implementer: String?,
)

/** Utilisation, derived from idle residency rather than the denied /proc/stat. */
data class CpuLoad(
    /** Busy fraction per core, 0f..1f. */
    val perCore: List<Float>,
) {
    val average: Float get() = if (perCore.isEmpty()) 0f else perCore.average().toFloat()
}

/** A point-in-time snapshot used to derive [CpuLoad] from two samples. */
data class CpuIdleSample(
    val elapsedNanos: Long,
    /** Summed idle microseconds per core, across every cpuidle state. */
    val idleMicrosPerCore: List<Long>,
)
