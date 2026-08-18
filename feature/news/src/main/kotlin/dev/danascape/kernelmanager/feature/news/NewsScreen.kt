// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.danascape.kernelmanager.core.designsystem.R as DesignSystemR
import dev.danascape.kernelmanager.feature.news.R
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.common.LoadError
import dev.danascape.kernelmanager.core.model.NewsPost
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.component.topInset
import dev.danascape.kernelmanager.core.designsystem.component.openArticle
import java.time.LocalDate

@Composable
fun NewsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = viewModel(factory = NewsViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    NewsContent(
        state = state,
        contentPadding = contentPadding,
        onPostClick = { post -> context.openArticle(post.url, toolbarColor) },
        onRetry = viewModel::refresh,
        modifier = modifier,
    )
}

/** Stateless body, so every state is previewable and testable on its own. */
@Composable
private fun NewsContent(
    state: NewsUiState,
    contentPadding: PaddingValues,
    onPostClick: (NewsPost) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        NewsUiState.Loading -> {
            CenteredContent(contentPadding, modifier) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        NewsUiState.Empty -> {
            CenteredContent(contentPadding, modifier) {
                Text(
                    text = stringResource(R.string.news_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SBTheme.colors.muted,
                    textAlign = TextAlign.Center,
                )
            }
        }

        is NewsUiState.Failed -> {
            CenteredContent(contentPadding, modifier) {
                LoadFailure(error = state.error, onRetry = onRetry)
            }
        }

        is NewsUiState.Ready -> {
            NewsList(
                posts = state.posts,
                stale = state.stale,
                contentPadding = contentPadding,
                onPostClick = onPostClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun NewsList(
    posts: List<NewsPost>,
    stale: Boolean,
    contentPadding: PaddingValues,
    onPostClick: (NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(top = contentPadding.topInset()),
        contentPadding =
            contentPadding.expandedBy(
                horizontal = 16.dp,
                top = 24.dp,
                bottom = 24.dp,
                includeTopInset = false,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") { Header() }

        if (stale) {
            item(key = "stale") { OfflineNotice() }
        }

        newsItems(posts, onPostClick)
    }
}

/** Only the newest post carries its cover, matching the website. */
private fun LazyListScope.newsItems(
    posts: List<NewsPost>,
    onPostClick: (NewsPost) -> Unit,
) {
    val featuredId = posts.firstOrNull()?.id
    itemsIndexed(items = posts, key = { _, post -> post.id }) { index, post ->
        NewsCard(
            post = post,
            onClick = { onPostClick(post) },
            showCover = post.id == featuredId,
            revealIndex = index,
        )
    }
}

@Composable
private fun Header() {
    Column(
        modifier =
            Modifier
                .pvotReveal(0)
                .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.news_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
        )
        Text(
            text = stringResource(R.string.news_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.news_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = SBTheme.colors.muted,
        )
    }
}

/** Says plainly that the list is a saved copy. */
@Composable
private fun OfflineNotice() {
    Text(
        text = stringResource(R.string.news_offline_copy),
        style = MaterialTheme.typography.labelSmall,
        color = SBTheme.colors.faint,
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 12.dp, vertical = 10.dp),
    )
}

@Composable
private fun LoadFailure(
    error: LoadError,
    onRetry: () -> Unit,
) {
    val (title, message) =
        when (error) {
            LoadError.OFFLINE -> {
                stringResource(R.string.news_error_offline_title) to
                    stringResource(R.string.news_error_offline_message)
            }

            LoadError.SERVER -> {
                stringResource(R.string.news_error_server_title) to
                    stringResource(R.string.news_error_server_message)
            }

            LoadError.MALFORMED -> {
                stringResource(R.string.news_error_malformed_title) to
                    stringResource(R.string.news_error_malformed_message)
            }
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(horizontal = 32.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = SBTheme.colors.muted,
            textAlign = TextAlign.Center,
        )
        OutlinedButton(onClick = onRetry) {
            Text(
                text = stringResource(DesignSystemR.string.action_retry),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun CenteredContent(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(contentPadding),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

private val PreviewPosts =
    listOf(
        NewsPost(
            id = "2026-08-12-stormbreaker-clang-default",
            title = "StormBreaker Clang is now our default toolchain",
            date = LocalDate.of(2026, 8, 12),
            tag = "Toolchain",
            author = "Saalim Quadri",
            summary =
                "Our in-house LLVM/Clang build is back, and from here on it " +
                    "compiles every StormBreaker kernel by default.",
            coverUrl = null,
            url = "https://stormbreaker.squadri.me/news/2026-08-12-stormbreaker-clang-default/",
            readingMinutes = 2,
        ),
        NewsPost(
            id = "2026-08-11-we-are-alive",
            title = "We're alive, and we're building again",
            date = LocalDate.of(2026, 8, 11),
            tag = "Announcement",
            author = "Saalim Quadri",
            summary = "It went quiet for a while. It never went away.",
            coverUrl = null,
            url = "https://stormbreaker.squadri.me/news/2026-08-11-we-are-alive/",
            readingMinutes = 3,
        ),
    )

@Preview
@Composable
private fun NewsReadyPreview() {
    SBTheme {
        NewsContent(
            state = NewsUiState.Ready(PreviewPosts, stale = false),
            contentPadding = PaddingValues(),
            onPostClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun NewsOfflinePreview() {
    SBTheme {
        NewsContent(
            state = NewsUiState.Ready(PreviewPosts, stale = true),
            contentPadding = PaddingValues(),
            onPostClick = {},
            onRetry = {},
        )
    }
}

@Preview
@Composable
private fun NewsFailedPreview() {
    SBTheme {
        NewsContent(
            state = NewsUiState.Failed(LoadError.OFFLINE),
            contentPadding = PaddingValues(),
            onPostClick = {},
            onRetry = {},
        )
    }
}
