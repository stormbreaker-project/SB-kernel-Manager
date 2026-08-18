// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.batterymonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Brings the monitor back after the two events that silently kill it.
 *
 * A reboot stops every service, and replacing the package during an app update
 * kills the process — in both cases the notification simply disappears and
 * nothing restarts it, so a session the user had running is lost without any
 * indication.
 *
 * Only restarts if the user had it switched on; this never turns it on by
 * itself.
 */
class BatteryMonitorRestartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            -> restartIfEnabled(context.applicationContext)
        }
    }

    private fun restartIfEnabled(context: Context) {
        // Reading the preference is IO, so hold the broadcast open for it.
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (BatterySessionStore(context).enabled.first()) {
                    BatteryMonitorService.start(context)
                }
            } catch (_: Exception) {
                // A background start can be refused depending on the exemption
                // that applies to the broadcast. Opening the app recovers it.
            } finally {
                pending.finish()
            }
        }
    }
}
