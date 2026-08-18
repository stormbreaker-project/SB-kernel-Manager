// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.monitor

import java.util.Locale
import kotlin.math.roundToInt

private const val PERCENT = 100

private const val KHZ_PER_GHZ = 1_000_000f
private const val KHZ_PER_MHZ = 1000

private const val BYTES_PER_GB = 1_073_741_824f
private const val BYTES_PER_MB = 1_048_576f
private const val BYTES_PER_KB = 1024f

private const val MILLIS_PER_MINUTE = 60_000
private const val MINUTES_PER_DAY = 1440
private const val MINUTES_PER_HOUR = 60

internal fun percent(fraction: Float): String = "${(fraction * PERCENT).roundToInt()}%"

internal fun frequency(khz: Int): String =
    if (khz >= KHZ_PER_GHZ) {
        String.format(Locale.getDefault(), "%.2f GHz", khz / KHZ_PER_GHZ)
    } else {
        String.format(Locale.getDefault(), "%d MHz", khz / KHZ_PER_MHZ)
    }

internal fun bytes(value: Long): String {
    val gb = value / BYTES_PER_GB
    val mb = value / BYTES_PER_MB
    val kb = value / BYTES_PER_KB
    return when {
        gb >= 1f -> String.format(Locale.getDefault(), "%.1f GB", gb)
        mb >= 1f -> String.format(Locale.getDefault(), "%.1f MB", mb)
        kb >= 1f -> String.format(Locale.getDefault(), "%.0f KB", kb)
        else -> "$value B"
    }
}

/** Coarse by design: a glance wants "2h 24m", not seconds ticking. */
internal fun duration(millis: Long): String {
    val totalMinutes = millis / MILLIS_PER_MINUTE
    val days = totalMinutes / MINUTES_PER_DAY
    val hours = (totalMinutes % MINUTES_PER_DAY) / MINUTES_PER_HOUR
    val minutes = totalMinutes % MINUTES_PER_HOUR
    return when {
        days > 0 -> "${days}d ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}
