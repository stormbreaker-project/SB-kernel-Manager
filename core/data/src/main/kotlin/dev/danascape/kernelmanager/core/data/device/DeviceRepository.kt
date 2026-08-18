package dev.danascape.kernelmanager.core.data.device

import dev.danascape.kernelmanager.core.device.DeviceIdentityReader
import dev.danascape.kernelmanager.core.device.SystemVitalsReader
import dev.danascape.kernelmanager.core.model.DeviceIdentity
import dev.danascape.kernelmanager.core.model.Vitals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceRepository(private val vitalsReader: SystemVitalsReader) {

    /** Fixed for the life of the process. */
    val identity: DeviceIdentity by lazy { DeviceIdentityReader.read() }

    /** Touches sysfs, so off the main thread. */
    suspend fun vitals(): Vitals = withContext(Dispatchers.IO) { vitalsReader.read() }
}
