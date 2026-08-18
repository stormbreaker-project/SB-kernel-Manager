// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.batterymonitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import dev.danascape.kernelmanager.core.battery.BatterySession
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

internal const val CHANNEL_ID = "battery_monitor"

private const val FIELD_SEPARATOR = "   ·   "

private const val PERCENT = 100
private const val MICROAMPS_PER_MILLIAMP = 1000
private const val MILLIS_PER_SECOND = 1000
private const val SECONDS_PER_HOUR = 3600
private const val SECONDS_PER_MINUTE = 60

/** Renders the session as an ongoing notification. */
internal class BatteryNotification(
    private val context: Context,
) {
    fun ensureChannel() {
        val manager = context.getSystemService<NotificationManager>() ?: return
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.battery_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = context.getString(R.string.battery_channel_description)
                setShowBadge(false)
            }
        manager.createNotificationChannel(channel)
    }

    fun build(
        session: BatterySession?,
        smallIcon: Int,
    ): Notification {
        val builder =
            NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setOngoing(true)
                .setSilent(true)
                .setShowWhen(false)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (session?.latest == null) {
            return builder.setContentTitle(context.getString(R.string.battery_waiting)).build()
        }

        return builder
            .setContentTitle(summaryLine(session))
            .setSubText(duration(session.sessionMillis))
            .setStyle(
                NotificationCompat.InboxStyle().also { style ->
                    detailLines(session).forEach(style::addLine)
                },
            ).build()
    }

    /** Level · temperature · charge state · current. */
    private fun summaryLine(session: BatterySession): String {
        val sample = session.latest ?: return ""
        return buildList {
            add(context.getString(R.string.battery_level, sample.levelPercent))
            sample.temperatureC?.let { add(context.getString(R.string.battery_temp, it)) }
            add(
                context.getString(
                    if (sample.charging) R.string.battery_charging else R.string.battery_discharging,
                ),
            )
            sample.currentMicroAmps?.let {
                add(context.getString(R.string.battery_current, abs(it) / MICROAMPS_PER_MILLIAMP))
            }
        }.joinToString(FIELD_SEPARATOR)
    }

    private fun detailLines(session: BatterySession): List<String> {
        val sessionMillis = session.screenOnMillis + session.screenOffMillis
        val screenOff = session.screenOffMillis

        return listOf(
            context.getString(
                R.string.battery_drain_rates,
                session.activeDrainPerHour?.let(::rate) ?: pending(),
                session.idleDrainPerHour?.let(::rate) ?: pending(),
            ),
            context.getString(
                R.string.battery_screen_on,
                duration(session.screenOnMillis),
                share(session.screenOnMillis, sessionMillis),
            ),
            context.getString(
                R.string.battery_screen_off,
                duration(screenOff),
                share(screenOff, sessionMillis),
            ),
            context.getString(
                R.string.battery_deep_sleep,
                duration(session.screenOffDeepSleepMillis),
                share(session.screenOffDeepSleepMillis, screenOff),
            ),
            context.getString(
                R.string.battery_awake,
                duration(session.screenOffAwakeMillis),
                share(session.screenOffAwakeMillis, screenOff),
            ),
        )
    }

    private fun share(
        part: Long,
        whole: Long,
    ): Int = if (whole <= 0) 0 else ((part.toFloat() / whole) * PERCENT).roundToInt()

    private fun rate(value: Float) = context.getString(R.string.battery_rate_value, value)

    private fun pending() = context.getString(R.string.battery_rate_pending)

    /** Zero units are dropped, so a fresh session reads "12s" rather than "0h 0m 12s". */
    private fun duration(millis: Long): String {
        val seconds = millis / MILLIS_PER_SECOND
        val h = seconds / SECONDS_PER_HOUR
        val m = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE
        val s = seconds % SECONDS_PER_MINUTE
        return when {
            h > 0 -> String.format(Locale.getDefault(), "%dh %dm %ds", h, m, s)
            m > 0 -> String.format(Locale.getDefault(), "%dm %ds", m, s)
            else -> String.format(Locale.getDefault(), "%ds", s)
        }
    }
}
