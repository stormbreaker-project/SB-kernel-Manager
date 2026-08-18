// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.network.dto

import dev.danascape.kernelmanager.core.model.NewsPost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeParseException

/** Wire shape of `/api/v1/news.json`. */
@Serializable
data class NewsFeedDto(
    val schema: Int = 0,
    val posts: List<NewsPostDto> = emptyList(),
)

@Serializable
data class NewsPostDto(
    val id: String,
    val title: String,
    val date: String,
    val tag: String? = null,
    val author: String? = null,
    val summary: String? = null,
    val cover: String? = null,
    val url: String,
    @SerialName("reading_minutes") val readingMinutes: Int = 1,
)

/** Maps the feed to domain posts, dropping any entry that cannot be represented. */
fun NewsFeedDto.toDomain(): List<NewsPost> = posts.mapNotNull { it.toDomainOrNull() }

private fun NewsPostDto.toDomainOrNull(): NewsPost? {
    val parsedDate =
        try {
            LocalDate.parse(date)
        } catch (_: DateTimeParseException) {
            return null
        }
    return NewsPost(
        id = id,
        title = title,
        date = parsedDate,
        tag = tag,
        author = author,
        summary = summary,
        coverUrl = cover,
        url = url,
        readingMinutes = readingMinutes.coerceAtLeast(1),
    )
}
