package dev.danascape.kernelmanager.feature.licenses

/**
 * Third-party notices.
 *
 * Curated by hand rather than generated. A generator (AboutLibraries and the
 * like) would add a plugin and a build step to restate what is a short, slow
 * moving list; the tradeoff is that adding a dependency means adding a line
 * here. Every entry below ships inside the APK.
 */
data class License(
    val name: String,
    val holder: String,
    val license: String,
    val url: String,
)

val THIRD_PARTY_LICENSES: List<License> = listOf(
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
    License(
        "JetBrains Mono",
        "The JetBrains Mono Project Authors",
        "SIL OFL 1.1",
        "https://github.com/JetBrains/JetBrainsMono",
    ),
    License(
        "Space Grotesk",
        "The Space Grotesk Project Authors",
        "SIL OFL 1.1",
        "https://github.com/floriankarsten/space-grotesk",
    ),
)
