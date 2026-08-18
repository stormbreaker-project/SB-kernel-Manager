// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import dev.danascape.kernelmanager.core.model.CpuIdleSample
import dev.danascape.kernelmanager.core.model.CpuLoad
import java.io.File
import java.util.concurrent.TimeUnit

private const val CPU_ROOT = "/sys/devices/system/cpu"

/** Derives CPU utilisation from idle residency. */
object CpuLoadReader {
    fun sample(): CpuIdleSample? {
        val cores =
            File(CPU_ROOT)
                .listFiles { file -> file.name.matches(Regex("cpu\\d+")) }
                ?.sortedBy { it.name.removePrefix("cpu").toIntOrNull() ?: 0 }
                ?: return null
        if (cores.isEmpty()) return null

        val idle = cores.map { core -> summedIdleMicros(core) }
        if (idle.all { it == 0L }) return null

        return CpuIdleSample(elapsedNanos = System.nanoTime(), idleMicrosPerCore = idle)
    }

    /** Busy fraction per core between two samples. */
    fun load(
        first: CpuIdleSample,
        second: CpuIdleSample,
    ): CpuLoad? {
        val windowMicros = TimeUnit.NANOSECONDS.toMicros(second.elapsedNanos - first.elapsedNanos)
        if (windowMicros <= 0) return null
        if (first.idleMicrosPerCore.size != second.idleMicrosPerCore.size) return null

        val perCore =
            first.idleMicrosPerCore.indices.map { index ->
                val idleDelta = second.idleMicrosPerCore[index] - first.idleMicrosPerCore[index]
                val busy = 1f - (idleDelta.toFloat() / windowMicros)
                busy.coerceIn(0f, 1f)
            }
        return CpuLoad(perCore)
    }

    private fun summedIdleMicros(core: File): Long {
        val states =
            core.resolve("cpuidle").listFiles { file ->
                file.name.startsWith("state")
            } ?: return 0L
        return states.sumOf { state -> state.resolve("time").readTextOrNull()?.toLongOrNull() ?: 0L }
    }
}
