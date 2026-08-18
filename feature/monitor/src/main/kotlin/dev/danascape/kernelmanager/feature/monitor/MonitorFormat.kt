package dev.danascape.kernelmanager.feature.monitor

import java.util.Locale
import kotlin.math.roundToInt

internal fun percent(fraction: Float): String = "${(fraction * 100).roundToInt()}%"

internal fun frequency(khz: Int): String = if (khz >= 1_000_000) {
    String.format(Locale.getDefault(), "%.2f GHz", khz / 1_000_000f)
} else {
    String.format(Locale.getDefault(), "%d MHz", khz / 1000)
}

internal fun bytes(value: Long): String {
    val gb = value / 1_073_741_824f
    val mb = value / 1_048_576f
    val kb = value / 1024f
    return when {
        gb >= 1f -> String.format(Locale.getDefault(), "%.1f GB", gb)
        mb >= 1f -> String.format(Locale.getDefault(), "%.1f MB", mb)
        kb >= 1f -> String.format(Locale.getDefault(), "%.0f KB", kb)
        else -> "$value B"
    }
}

internal fun duration(millis: Long): String {
    val totalMinutes = millis / 60_000
    val days = totalMinutes / 1440
    val hours = (totalMinutes % 1440) / 60
    val minutes = totalMinutes % 60
    return when {
        days > 0 -> "${days}d ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}
