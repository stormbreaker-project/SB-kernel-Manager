package dev.danascape.kernelmanager.feature.downloads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun DownloadsScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        kicker = "BUILDS",
        title = "Builds",
        description = "Kernel builds and changelogs, with checksums, for your device.",
        modifier = modifier,
    )
}

@Preview
@Composable
private fun DownloadsScreenPreview() {
    SBTheme { DownloadsScreen() }
}
