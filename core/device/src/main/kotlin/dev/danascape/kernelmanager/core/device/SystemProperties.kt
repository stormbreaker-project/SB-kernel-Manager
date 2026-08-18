// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

/** Android system properties. */
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
