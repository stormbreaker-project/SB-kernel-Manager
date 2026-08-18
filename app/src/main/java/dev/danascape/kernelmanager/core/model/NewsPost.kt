package dev.danascape.kernelmanager.core.model

import java.time.LocalDate

/**
 * A newsroom post, as the app understands it.
 *
 * Kept separate from the wire type so the JSON contract can gain fields, relax
 * nullability, or rename things without that reaching the UI.
 */
data class NewsPost(
    val id: String,
    val title: String,
    val date: LocalDate,
    val tag: String?,
    val author: String?,
    val summary: String?,
    val coverUrl: String?,
    /** Canonical article on the website — what the reader is sent to. */
    val url: String,
    val readingMinutes: Int,
)

/** Why a load failed, as a type. Mapping to human text is the UI's job. */
enum class LoadError {
    /** No usable network and nothing cached to fall back on. */
    OFFLINE,

    /** Reached the host, but it did not return a usable response. */
    SERVER,

    /** Got a response we could not parse — a contract break on our side. */
    MALFORMED,
}
