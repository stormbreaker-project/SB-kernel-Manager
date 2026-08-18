package dev.danascape.kernelmanager.feature.more

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun MoreScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        kicker = "PROJECT",
        title = "More",
        description = "Community, contributing, donations, and about Team StormBreaker.",
        modifier = modifier,
    )
}

@Preview
@Composable
private fun MoreScreenPreview() {
    SBTheme { MoreScreen() }
}
