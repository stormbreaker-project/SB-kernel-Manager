// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import dev.danascape.kernelmanager.core.model.CpuIdleSample
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class CpuLoadReaderTest {
    private fun sampleAt(
        seconds: Long,
        vararg idleMicros: Long,
    ) = CpuIdleSample(
        elapsedNanos = TimeUnit.SECONDS.toNanos(seconds),
        idleMicrosPerCore = idleMicros.toList(),
    )

    @Test
    fun `a fully idle core reads as zero busy`() {
        // One second of wall time, one second of idle.
        val load = CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 1_000_000))!!
        assertEquals(0f, load.perCore.single(), 0.001f)
    }

    @Test
    fun `a core that never idles reads as fully busy`() {
        val load = CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 0))!!
        assertEquals(1f, load.perCore.single(), 0.001f)
    }

    @Test
    fun `half the window spent idle reads as half busy`() {
        val load = CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 500_000))!!
        assertEquals(0.5f, load.perCore.single(), 0.001f)
    }

    @Test
    fun `idle exceeding the window clamps to idle rather than going negative`() {
        // Sampling overhead makes the real window longer than intended, so idle
        // deltas can exceed it; that must not read as a negative or wrapped load.
        val load = CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 1_200_000))!!
        assertEquals(0f, load.perCore.single(), 0.001f)
    }

    @Test
    fun `average spans all cores`() {
        val load =
            CpuLoadReader.load(
                sampleAt(0, 0, 0),
                sampleAt(1, 1_000_000, 0),
            )!!
        assertEquals(0.5f, load.average, 0.001f)
    }

    @Test
    fun `samples taken out of order produce nothing`() {
        assertNull(CpuLoadReader.load(sampleAt(1, 0), sampleAt(0, 0)))
    }

    @Test
    fun `a changed core count produces nothing rather than a mismatched read`() {
        assertNull(CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 0, 0)))
    }
}
