// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val BYTES_PER_UNIT = 1024.0
private val BYTE_UNITS = listOf("B", "KB", "MB", "GB", "TB")
private const val KHZ_PER_GHZ = 1_000_000f
private const val GIGABYTE_UNIT_INDEX = 3
private const val DECIMAL_THRESHOLD = 100
private val BUILD_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())

internal fun Float.decimals(places: Int): String = String.format(Locale.US, "%.${places}f", this)

internal fun Int.khzAsGhz(): String = String.format(Locale.US, "%.2f GHz", this / KHZ_PER_GHZ)

internal fun Long.asByteSize(): String {
    var value = toDouble()
    var unit = 0
    while (value >= BYTES_PER_UNIT && unit < BYTE_UNITS.lastIndex) {
        value /= BYTES_PER_UNIT
        unit++
    }
    val places = if (unit >= GIGABYTE_UNIT_INDEX && value < DECIMAL_THRESHOLD) 1 else 0
    return String.format(Locale.US, "%.${places}f %s", value, BYTE_UNITS[unit])
}

internal fun Long.asBuildDate(): String = BUILD_DATE.format(Instant.ofEpochMilli(this))

/** Joins a float list for display, dropping the noise of repeated identical entries. */
internal fun List<Float>.joinDistinct(
    places: Int,
    suffix: String = "",
): String =
    distinct()
        .sorted()
        .joinToString(", ") { "${it.decimals(places)}$suffix" }
