// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.network.dto

import dev.danascape.kernelmanager.core.network.SBJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Guards the contract with `/api/v1/links.json` and the bundled fallback copy. */
class LinksDtoTest {

    @Test
    fun `decodes a section as published by the site`() {
        val dto = SBJson.decodeFromString<LinksDto>(
            """
            {"schema":1,"sections":[{"id":"community","title":"Community","items":[
              {"id":"telegram","label":"Telegram","description":"Release updates and support",
               "url":"https://t.me/x","external":true,"soon":false}
            ]}]}
            """.trimIndent(),
        )

        val section = dto.toDomain().single()
        assertEquals("community", section.id)
        val item = section.items.single()
        assertEquals("Telegram", item.label)
        assertTrue(item.external)
        assertTrue(item.openable)
    }

    @Test
    fun `an item announced but not published is kept and not openable`() {
        val dto = SBJson.decodeFromString<LinksDto>(
            """
            {"schema":1,"sections":[{"id":"project","title":"Project","items":[
              {"id":"docs","label":"Documentation","url":null,"soon":true}
            ]}]}
            """.trimIndent(),
        )

        val item = dto.toDomain().single().items.single()
        assertTrue(item.soon)
        assertFalse(item.openable)
    }

    @Test
    fun `an item with neither a url nor a soon flag is dropped`() {
        val dto = SBJson.decodeFromString<LinksDto>(
            """
            {"schema":1,"sections":[{"id":"project","title":"Project","items":[
              {"id":"broken","label":"Broken"},
              {"id":"ok","label":"OK","url":"https://example.invalid/"}
            ]}]}
            """.trimIndent(),
        )

        assertEquals(listOf("ok"), dto.toDomain().single().items.map { it.id })
    }

    @Test
    fun `a section left empty by filtering disappears entirely`() {
        val dto = SBJson.decodeFromString<LinksDto>(
            """
            {"schema":1,"sections":[
              {"id":"empty","title":"Empty","items":[{"id":"broken","label":"Broken"}]},
              {"id":"kept","title":"Kept","items":[{"id":"ok","label":"OK","url":"https://example.invalid/"}]}
            ]}
            """.trimIndent(),
        )

        assertEquals(listOf("kept"), dto.toDomain().map { it.id })
    }

    @Test
    fun `ignores fields this version does not know about`() {
        val dto = SBJson.decodeFromString<LinksDto>(
            """
            {"schema":1,"analytics":true,"sections":[{"id":"a","title":"A","badge":"new","items":[
              {"id":"x","label":"X","url":"https://example.invalid/","icon":"telegram"}
            ]}]}
            """.trimIndent(),
        )

        assertEquals("X", dto.toDomain().single().items.single().label)
    }
}
