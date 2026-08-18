package dev.danascape.kernelmanager.core.network.dto

import dev.danascape.kernelmanager.core.network.SBJson
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The bundled copy of links.json is the floor under the More screen: it is what
 * renders on first run with no network. If it stops parsing, that screen is
 * silently empty offline, and nothing else would catch it.
 */
class BundledLinksTest {

    private val asset = File("src/main/assets/links.json")

    @Test
    fun `the shipped asset exists and parses`() {
        assertTrue("missing ${asset.absolutePath}", asset.exists())

        val sections = SBJson.decodeFromString<LinksDto>(asset.readText()).toDomain()

        assertTrue("no sections survived mapping", sections.isNotEmpty())
        assertTrue(
            "every section must have at least one item",
            sections.all { it.items.isNotEmpty() },
        )
        assertTrue(
            "every item must be openable or explicitly marked soon",
            sections.flatMap { it.items }.all { it.openable || it.soon },
        )
    }
}
