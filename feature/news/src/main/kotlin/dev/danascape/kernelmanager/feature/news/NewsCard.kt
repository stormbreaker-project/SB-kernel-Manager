// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.danascape.kernelmanager.feature.news.R
import com.prauga.pvot.designsystem.modifier.pvotPressScale
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.NewsPost
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

/** Matches the outer radius of the grouped rows on More. */
private val CardRadius = 24.dp

/**
 * One post, laid out like the website's news card: a mono meta line, the
 * headline, the summary, then a read affordance.
 *
 * A filled surface rather than an outlined box, on the same shape and motion
 * vocabulary as the grouped rows on More.
 */
@Composable
fun NewsCard(
    post: NewsPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showCover: Boolean = false,
    revealIndex: Int = 0,
) {
    val openLabel = stringResource(R.string.news_open_article, post.title)
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(CardRadius)

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .pvotReveal(revealIndex)
                .pvotPressScale(interactionSource)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ).semantics { contentDescription = openLabel },
    ) {
        if (showCover && post.coverUrl != null) {
            AsyncImage(
                model = post.coverUrl,
                // Decorative: the headline below carries the meaning.
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(SBTheme.colors.ink),
                contentScale = ContentScale.Crop,
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MetaLine(post)

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            post.summary?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SBTheme.colors.muted,
                )
            }

            Text(
                text = "${stringResource(R.string.news_read)} →",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun MetaLine(post: NewsPost) {
    val separator = stringResource(R.string.news_meta_separator)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetaText(post.date.format(DateFormat))
        MetaText(separator)
        MetaText(
            pluralStringResource(
                R.plurals.news_reading_time,
                post.readingMinutes,
                post.readingMinutes,
            ),
        )
        post.tag?.let {
            MetaText(separator)
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = SBTheme.colors.accent,
            )
        }
    }
}

@Composable
private fun MetaText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = SBTheme.colors.faint,
    )
}

@Preview
@Composable
private fun NewsCardPreview() {
    SBTheme {
        NewsCard(
            post =
                NewsPost(
                    id = "2026-08-11-we-are-alive",
                    title = "We're alive, and we're building again",
                    date = LocalDate.of(2026, 8, 11),
                    tag = "Announcement",
                    author = "Saalim Quadri",
                    summary =
                        "It went quiet for a while. It never went away. " +
                            "New home, revived charter, and the work is moving again.",
                    coverUrl = null,
                    url = "https://stormbreaker.squadri.me/news/2026-08-11-we-are-alive/",
                    readingMinutes = 3,
                ),
            onClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
