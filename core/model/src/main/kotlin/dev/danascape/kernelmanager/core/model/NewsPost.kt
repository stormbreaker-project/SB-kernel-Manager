// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

import java.time.LocalDate

/** A newsroom post. Kept apart from the wire type so the JSON contract can move independently. */
data class NewsPost(
    val id: String,
    val title: String,
    val date: LocalDate,
    val tag: String?,
    val author: String?,
    val summary: String?,
    val coverUrl: String?,
    val url: String,
    val readingMinutes: Int,
)
