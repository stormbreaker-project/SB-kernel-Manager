// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.data.device

import dev.danascape.kernelmanager.core.device.CpuLoadReader
import dev.danascape.kernelmanager.core.device.DeviceDetailsReader
import dev.danascape.kernelmanager.core.device.DeviceProfileReader
import dev.danascape.kernelmanager.core.device.SystemProperties
import dev.danascape.kernelmanager.core.device.SystemVitalsReader
import dev.danascape.kernelmanager.core.model.DeviceDetails
import dev.danascape.kernelmanager.core.model.DeviceProfile
import dev.danascape.kernelmanager.core.model.Vitals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val LOAD_WINDOW_MILLIS = 500L

class DeviceRepository(
    private val profileReader: DeviceProfileReader,
    private val detailsReader: DeviceDetailsReader,
    private val vitalsReader: SystemVitalsReader,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /** Read once and cached. */
    suspend fun profile(): DeviceProfile =
        cachedProfile ?: withContext(ioDispatcher) {
            profileReader.read().also { cachedProfile = it }
        }

    @Volatile
    private var cachedProfile: DeviceProfile? = null

    /** Read once and cached, like the profile. */
    suspend fun details(): DeviceDetails =
        cachedDetails ?: withContext(ioDispatcher) {
            detailsReader.read(SystemProperties.read()).also { cachedDetails = it }
        }

    @Volatile
    private var cachedDetails: DeviceDetails? = null

    /** Suspends for the sampling window: utilisation needs two spaced samples. */
    suspend fun vitals(): Vitals =
        withContext(ioDispatcher) {
            val first = CpuLoadReader.sample()
            delay(LOAD_WINDOW_MILLIS)
            val load = first?.let { start -> CpuLoadReader.sample()?.let { CpuLoadReader.load(start, it) } }
            vitalsReader.read(load)
        }
}
