package dev.danascape.kernelmanager.core.network.dto

import dev.danascape.kernelmanager.core.network.SBJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

/**
 * Guards the contract with `/api/v1/news.json`.
 *
 * The site can be deployed independently of any installed app, so the decoder
 * has to survive fields being added and individual posts being malformed.
 */
class NewsFeedDtoTest {

    @Test
    fun `decodes a post as published by the site`() {
        val feed = SBJson.decodeFromString<NewsFeedDto>(
            """
            {
              "schema": 1,
              "posts": [{
                "id": "2026-08-11-we-are-alive",
                "title": "We're alive, and we're building again",
                "date": "2026-08-11",
                "tag": "Announcement",
                "author": "Saalim Quadri",
                "summary": "It went quiet for a while.",
                "cover": "https://stormbreaker.squadri.me/news/alive.svg",
                "url": "https://stormbreaker.squadri.me/news/2026-08-11-we-are-alive/",
                "reading_minutes": 3
              }]
            }
            """.trimIndent(),
        )

        val post = feed.toDomain().single()
        assertEquals(1, feed.schema)
        assertEquals("2026-08-11-we-are-alive", post.id)
        assertEquals(LocalDate.of(2026, 8, 11), post.date)
        assertEquals("Announcement", post.tag)
        assertEquals(3, post.readingMinutes)
        assertEquals("https://stormbreaker.squadri.me/news/alive.svg", post.coverUrl)
    }

    @Test
    fun `ignores fields this version does not know about`() {
        val feed = SBJson.decodeFromString<NewsFeedDto>(
            """
            {
              "schema": 1,
              "unexpected_top_level": true,
              "posts": [{
                "id": "a", "title": "A", "date": "2026-08-11",
                "url": "https://example.invalid/a/",
                "body_markdown": "added later",
                "translations": { "hi": "..." }
              }]
            }
            """.trimIndent(),
        )

        assertEquals("A", feed.toDomain().single().title)
    }

    @Test
    fun `omitted optional fields decode as null`() {
        val feed = SBJson.decodeFromString<NewsFeedDto>(
            """
            {"schema":1,"posts":[{
              "id":"a","title":"A","date":"2026-08-11",
              "url":"https://example.invalid/a/"
            }]}
            """.trimIndent(),
        )

        val post = feed.toDomain().single()
        assertNull(post.tag)
        assertNull(post.author)
        assertNull(post.summary)
        assertNull(post.coverUrl)
        // Absent reading time still has to render, so it floors at one minute.
        assertEquals(1, post.readingMinutes)
    }

    @Test
    fun `an unparseable date costs that post, not the whole feed`() {
        val feed = SBJson.decodeFromString<NewsFeedDto>(
            """
            {"schema":1,"posts":[
              {"id":"bad","title":"Bad","date":"not-a-date","url":"https://example.invalid/bad/"},
              {"id":"good","title":"Good","date":"2026-08-12","url":"https://example.invalid/good/"}
            ]}
            """.trimIndent(),
        )

        assertEquals(listOf("good"), feed.toDomain().map { it.id })
    }

    @Test
    fun `a feed with no posts is empty rather than an error`() {
        val feed = SBJson.decodeFromString<NewsFeedDto>("""{"schema":1,"posts":[]}""")
        assertEquals(emptyList<String>(), feed.toDomain().map { it.id })
    }
}
