// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "DeviceReaders"

/**
 * Smoke test for the readers, and a dump of what this particular device gives up.
 *
 * Assertions stay deliberately loose: what is readable varies by vendor and
 * Android version, so this checks the readers do not crash or return nonsense,
 * rather than pinning values that are legitimately different elsewhere.
 */
@RunWith(AndroidJUnit4::class)
class DeviceReadersTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun dumpProfile() {
        val profile = DeviceProfileReader(context).read()

        Log.i(TAG, "=== IDENTITY ===")
        Log.i(TAG, "codename=${profile.identity.codename} model=${profile.identity.displayName}")
        Log.i(TAG, "kernel=${profile.identity.kernelRelease} sb=${profile.identity.isStormBreakerKernel}")

        Log.i(TAG, "=== OS ===")
        Log.i(TAG, "android=${profile.os.androidRelease} api=${profile.os.sdkInt} patch=${profile.os.securityPatch}")
        Log.i(TAG, "buildId=${profile.os.buildId} tags=${profile.os.tags} type=${profile.os.type}")
        Log.i(TAG, "rom=${profile.os.rom} stockSigned=${profile.os.isStockSigned}")

        Log.i(TAG, "=== BOOT ===")
        Log.i(TAG, "unlocked=${profile.boot.bootloaderUnlocked} vbs=${profile.boot.verifiedBootState} crypto=${profile.boot.encryption}")
        Log.i(TAG, "suBinary=${profile.suBinaryPresent}")

        Log.i(TAG, "=== SOC ===")
        Log.i(TAG, "platform=${profile.soc.platform} hw=${profile.soc.hardware}")
        Log.i(TAG, "soc=${profile.soc.manufacturer} ${profile.soc.model} abis=${profile.soc.supportedAbis}")

        Log.i(TAG, "=== CPU ===")
        profile.cpu?.let { cpu ->
            Log.i(TAG, "cores=${cpu.coreCount} clusters=${cpu.clusters.size} features=${cpu.features.size}")
            cpu.clusters.forEach { cluster ->
                Log.i(
                    TAG,
                    "policy${cluster.id} cores=${cluster.cores} " +
                        "${cluster.minKhz}-${cluster.maxKhz}kHz hwMax=${cluster.hardwareMaxKhz} " +
                        "opps=${cluster.availableKhz.size} gov=${cluster.governor} " +
                        "avail=${cluster.availableGovernors} part=${cluster.partId}",
                )
            }
        } ?: Log.i(TAG, "cpu topology unreadable")

        Log.i(TAG, "=== GPU ===")
        Log.i(TAG, "gpu=${profile.gpu}")

        // The identity fields come from Build and are always populated.
        assertTrue(profile.identity.codename.isNotBlank())
        assertTrue(profile.os.sdkInt > 0)
        // Every cluster must report a sane ceiling if it reports one at all.
        profile.cpu?.clusters?.forEach { cluster ->
            cluster.hardwareMaxKhz?.let { assertTrue("max looks wrong: $it", it in 100_000..10_000_000) }
        }
    }

    @Test
    fun dumpVitals() {
        val first = CpuLoadReader.sample()
        Thread.sleep(600)
        val load = first?.let { start -> CpuLoadReader.sample()?.let { CpuLoadReader.load(start, it) } }
        val vitals = SystemVitalsReader(context).read(load)

        Log.i(TAG, "=== VITALS ===")
        Log.i(TAG, "cpu=${vitals.cpu}")
        Log.i(TAG, "load=${vitals.load?.perCore?.map { "%.0f%%".format(it * 100) }} avg=${vitals.load?.average}")
        Log.i(TAG, "memory=${vitals.memory}")
        Log.i(TAG, "battery=${vitals.battery}")
        Log.i(TAG, "storage=${vitals.storage}")
        Log.i(TAG, "network=${vitals.network}")
        Log.i(TAG, "thermal=${vitals.thermal} uptimeMs=${vitals.uptimeMillis}")
        Log.i(
            TAG,
            "sleep: awake=${vitals.sleep.awakeMillis}ms deep=${vitals.sleep.deepSleepMillis}ms " +
                "(${"%.1f".format(vitals.sleep.deepSleepFraction * 100)}%)",
        )

        assertTrue(vitals.uptimeMillis > 0)
        // Awake can never exceed elapsed, or the sleep split is nonsense.
        assertTrue(vitals.sleep.awakeMillis <= vitals.sleep.elapsedMillis)
        // Derived load must be a real fraction, never negative or above one.
        vitals.load?.perCore?.forEach { assertTrue("load out of range: $it", it in 0f..1f) }
        vitals.memory?.let { assertTrue(it.usedBytes in 0..it.totalBytes) }
        vitals.battery?.let { assertTrue(it.percent in 0..100) }
    }

    @Test
    fun systemPropertiesAreReadable() {
        val properties = SystemProperties.read()
        Log.i(TAG, "=== PROPERTIES === count=${properties.size}")
        Log.i(TAG, "fingerprint=${properties["ro.build.fingerprint"]}")
        Log.i(TAG, "flash.locked=${properties["ro.boot.flash.locked"]} vbs=${properties["ro.boot.verifiedbootstate"]}")
        assertTrue("getprop returned nothing", properties.size > 100)
    }
}
