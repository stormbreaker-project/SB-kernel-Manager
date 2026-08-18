package dev.danascape.kernelmanager.feature.more

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun MoreScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SectionPlaceholder(
        kicker = "PROJECT",
        title = "More",
        description = "Community, contributing, donations, and about Team StormBreaker.",
        modifier = modifier.padding(contentPadding),
    )
}

@Preview
@Composable
private fun MoreScreenPreview() {
    SBTheme { MoreScreen(PaddingValues()) }
}
