package dev.danascape.kernelmanager.feature.news

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun NewsScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        kicker = "NEWS",
        title = "News",
        description = "Release notes and project updates, pulled from the same source as the website.",
        modifier = modifier,
    )
}

@Preview
@Composable
private fun NewsScreenPreview() {
    SBTheme { NewsScreen() }
}
