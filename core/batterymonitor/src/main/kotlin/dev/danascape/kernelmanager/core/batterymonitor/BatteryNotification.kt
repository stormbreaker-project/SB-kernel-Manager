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

internal const val CHANNEL_ID = "battery_monitor"

/**
 * Renders the session as an ongoing notification.
 *
 * Every line answers a different question, which is why they are not collapsed
 * into an average: active versus idle drain separates what the user spends from
 * what the device leaks, and deep sleep as a share of screen-off time is what
 * exposes a wakelock holding the device awake.
 */
internal class BatteryNotification(private val context: Context) {

    fun ensureChannel() {
        val manager = context.getSystemService<NotificationManager>() ?: return
        // Silent and low: this is a readout, not an alert.
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.battery_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.battery_channel_description)
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    fun build(session: BatterySession?, smallIcon: Int): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIcon)
            .setContentTitle(context.getString(R.string.battery_notification_title))
            .setOngoing(true)
            .setSilent(true)
            .setShowWhen(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (session?.latest == null) {
            return builder.setContentText(context.getString(R.string.battery_waiting)).build()
        }

        val lines = lines(session)
        return builder
            .setContentText(lines.first())
            .setStyle(
                NotificationCompat.InboxStyle().also { style ->
                    lines.forEach(style::addLine)
                },
            )
            .setSubText(duration(session.sessionMillis))
            .build()
    }

    private fun lines(session: BatterySession): List<String> {
        val sample = session.latest ?: return emptyList()
        val now = buildList {
            sample.currentMicroAmps?.let {
                add(context.getString(R.string.battery_now_current, abs(it) / 1000))
            }
            session.watts?.let { add(context.getString(R.string.battery_now_watts, it)) }
            sample.temperatureC?.let { add(context.getString(R.string.battery_now_temp, it)) }
            if (sample.charging) {
                add(context.getString(R.string.battery_charging))
            } else {
                session.estimatedMillisRemaining?.let {
                    add(context.getString(R.string.battery_now_remaining, duration(it)))
                }
            }
        }.joinToString(" · ")

        return listOf(
            context.getString(R.string.battery_now, now),
            context.getString(
                R.string.battery_rates,
                session.activeDrainPerHour?.let { rate(it) } ?: pending(),
                session.idleDrainPerHour?.let { rate(it) } ?: pending(),
            ),
            context.getString(
                R.string.battery_screen_on,
                duration(session.screenOnMillis),
                session.screenOnDrainedPercent,
                (session.screenOnDrainedMicroAmpHours / 1000).toInt(),
            ),
            context.getString(
                R.string.battery_screen_off,
                duration(session.screenOffMillis),
                session.screenOffDrainedPercent,
                (session.screenOffDrainedMicroAmpHours / 1000).toInt(),
            ),
            context.getString(
                R.string.battery_deep_sleep,
                duration(session.screenOffDeepSleepMillis),
                session.screenOffDeepSleepFraction * 100,
            ),
            context.getString(
                R.string.battery_awake,
                duration(session.screenOffAwakeMillis),
                (1f - session.screenOffDeepSleepFraction) * 100,
            ),
        )
    }

    private fun rate(value: Float) = context.getString(R.string.battery_rate_value, value)

    private fun pending() = context.getString(R.string.battery_rate_pending)

    private fun duration(millis: Long): String {
        val seconds = millis / 1000
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return when {
            h > 0 -> String.format(Locale.getDefault(), "%dh %dm %ds", h, m, s)
            m > 0 -> String.format(Locale.getDefault(), "%dm %ds", m, s)
            else -> String.format(Locale.getDefault(), "%ds", s)
        }
    }
}
