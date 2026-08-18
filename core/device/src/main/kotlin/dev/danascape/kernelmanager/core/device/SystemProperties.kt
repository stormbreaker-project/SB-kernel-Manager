// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

/**
 * Android system properties.
 *
 * There is no public API for these. `android.os.SystemProperties` is hidden and
 * on the unsupported list, so this shells out to `getprop` once and caches the
 * result — around 1100 entries, read in a single spawn rather than one per key.
 *
 * `Build` already covers most `ro.build.*` and `ro.product.*` values; this is
 * for the rest, notably `ro.boot.*`, which has no framework equivalent.
 */
class SystemProperties private constructor(
    private val values: Map<String, String>,
) {
    operator fun get(key: String): String? = values[key]?.takeIf { it.isNotBlank() }

    fun firstOf(vararg keys: String): Pair<String, String>? =
        keys.firstNotNullOfOrNull { key ->
            get(key)?.let { key to it }
        }

    val size: Int get() = values.size

    companion object {
        // getprop prints one entry per line as: [key]: [value]
        private val LINE = Regex("""^\[(.+?)]: \[(.*)]$""")

        fun read(): SystemProperties =
            SystemProperties(
                try {
                    ProcessBuilder("getprop")
                        .redirectErrorStream(true)
                        .start()
                        .inputStream
                        .bufferedReader()
                        .useLines { lines ->
                            lines
                                .mapNotNull { line ->
                                    LINE.matchEntire(line.trim())?.let { match ->
                                        match.groupValues[1] to match.groupValues[2]
                                    }
                                }.toMap()
                        }
                } catch (_: Exception) {
                    emptyMap()
                },
            )

        fun of(values: Map<String, String>): SystemProperties = SystemProperties(values)
    }
}
