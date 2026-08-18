// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.licenses

/** Third-party notices. */
data class License(
    val name: String,
    val holder: String,
    val license: String,
    val url: String,
)

val THIRD_PARTY_LICENSES: List<License> =
    listOf(
        License(
            "Jetpack Compose & AndroidX",
            "The Android Open Source Project",
            "Apache-2.0",
            "https://developer.android.com/jetpack/androidx",
        ),
        License(
            "Kotlin, kotlinx.coroutines, kotlinx.serialization",
            "JetBrains s.r.o. and contributors",
            "Apache-2.0",
            "https://github.com/JetBrains/kotlin",
        ),
        License(
            "Ktor",
            "JetBrains s.r.o.",
            "Apache-2.0",
            "https://github.com/ktorio/ktor",
        ),
        License(
            "OkHttp",
            "Square, Inc.",
            "Apache-2.0",
            "https://github.com/square/okhttp",
        ),
        License(
            "Coil",
            "Coil Contributors",
            "Apache-2.0",
            "https://github.com/coil-kt/coil",
        ),
        License(
            "Pvot design system",
            "Saalim Quadri",
            "Apache-2.0",
            "https://github.com/PVOT-OSS/PvotLib",
        ),
    )
