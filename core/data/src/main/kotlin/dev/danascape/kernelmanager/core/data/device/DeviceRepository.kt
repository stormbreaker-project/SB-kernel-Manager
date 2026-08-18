// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.data.device

import dev.danascape.kernelmanager.core.device.CpuLoadReader
import dev.danascape.kernelmanager.core.device.DeviceProfileReader
import dev.danascape.kernelmanager.core.device.SystemVitalsReader
import dev.danascape.kernelmanager.core.model.DeviceProfile
import dev.danascape.kernelmanager.core.model.Vitals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val LOAD_WINDOW_MILLIS = 500L

class DeviceRepository(
    private val profileReader: DeviceProfileReader,
    private val vitalsReader: SystemVitalsReader,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Read once and cached. Shelling out to `getprop` and walking cpufreq is
     * not something to repeat on every recomposition.
     */
    suspend fun profile(): DeviceProfile =
        cachedProfile ?: withContext(ioDispatcher) {
            profileReader.read().also { cachedProfile = it }
        }

    @Volatile
    private var cachedProfile: DeviceProfile? = null

    /**
     * Utilisation needs two idle samples spaced in time, so this suspends for
     * the sampling window rather than returning instantly.
     */
    suspend fun vitals(): Vitals =
        withContext(ioDispatcher) {
            val first = CpuLoadReader.sample()
            delay(LOAD_WINDOW_MILLIS)
            val load = first?.let { start -> CpuLoadReader.sample()?.let { CpuLoadReader.load(start, it) } }
            vitalsReader.read(load)
        }
}
