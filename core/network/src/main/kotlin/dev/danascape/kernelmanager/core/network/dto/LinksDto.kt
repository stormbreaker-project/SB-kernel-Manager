// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.network.dto

import dev.danascape.kernelmanager.core.model.LinkItem
import dev.danascape.kernelmanager.core.model.LinkSection
import kotlinx.serialization.Serializable

/** Wire shape of `/api/v1/links.json`, and of the bundled fallback copy. */
@Serializable
data class LinksDto(
    val schema: Int = 0,
    val sections: List<LinkSectionDto> = emptyList(),
)

@Serializable
data class LinkSectionDto(
    val id: String,
    val title: String,
    val items: List<LinkItemDto> = emptyList(),
)

@Serializable
data class LinkItemDto(
    val id: String,
    val label: String,
    val description: String? = null,
    val url: String? = null,
    val external: Boolean = false,
    val soon: Boolean = false,
)

/** Drops items with nothing to show and sections left empty by that. */
fun LinksDto.toDomain(): List<LinkSection> = sections
    .map { section ->
        LinkSection(
            id = section.id,
            title = section.title,
            items = section.items
                .filter { it.url != null || it.soon }
                .map {
                    LinkItem(
                        id = it.id,
                        label = it.label,
                        description = it.description,
                        url = it.url,
                        external = it.external,
                        soon = it.soon,
                    )
                },
        )
    }
    .filter { it.items.isNotEmpty() }
