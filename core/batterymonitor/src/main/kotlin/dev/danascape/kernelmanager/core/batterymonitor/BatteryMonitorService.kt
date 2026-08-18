// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.batterymonitor

import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import dev.danascape.kernelmanager.core.battery.BatteryTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val NOTIFICATION_ID = 1001

private const val SAMPLE_INTERVAL_MILLIS = 60_000L

/** Accumulates battery statistics for as long as the user leaves it on. */
class BatteryMonitorService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var sampler: BatterySampler
    private lateinit var store: BatterySessionStore
    private lateinit var notification: BatteryNotification
    private var loop: Job? = null

    private val transitions =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context?,
                intent: Intent?,
            ) {
                scope.launch { sampleOnce() }
            }
        }

    override fun onCreate() {
        super.onCreate()
        sampler = BatterySampler(this)
        store = BatterySessionStore(this)
        notification = BatteryNotification(this)
        notification.ensureChannel()

        registerReceiver(
            transitions,
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            },
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        running = true
        startInForeground()
        if (loop?.isActive != true) {
            loop =
                scope.launch {
                    while (isActive) {
                        sampleOnce()
                        delay(SAMPLE_INTERVAL_MILLIS)
                    }
                }
        }
        return START_STICKY
    }

    private suspend fun sampleOnce() {
        val sample = sampler.sample() ?: return
        val previous = store.previousSample()
        val session = BatteryTracker.fold(store.session(), previous, sample)
        store.save(session, sample)
        updateNotification()
    }

    private fun startInForeground() {
        val built = notification.build(null, applicationInfo.icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                built,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            ServiceCompat.startForeground(this, NOTIFICATION_ID, built, 0)
        }
    }

    /** A denied POST_NOTIFICATIONS silences the readout but not the service. */
    private suspend fun updateNotification() {
        if (!canPostNotifications()) return
        val session = store.session()
        val built = notification.build(session, applicationInfo.icon)
        try {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, built)
        } catch (_: SecurityException) {
        }
    }

    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        running = false
        runCatching { unregisterReceiver(transitions) }
        loop?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        @Volatile
        var running: Boolean = false
            private set

        /** @return false when the platform refused a background start. */
        fun start(context: Context): Boolean =
            try {
                context.startForegroundService(Intent(context, BatteryMonitorService::class.java))
                true
            } catch (_: Exception) {
                false
            }

        fun stop(context: Context) {
            running = false
            context.stopService(Intent(context, BatteryMonitorService::class.java))
        }
    }
}
