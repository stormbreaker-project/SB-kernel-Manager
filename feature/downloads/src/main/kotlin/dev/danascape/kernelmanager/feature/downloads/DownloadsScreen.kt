package dev.danascape.kernelmanager.feature.downloads

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.danascape.kernelmanager.feature.downloads.R
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun DownloadsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SectionPlaceholder(
        kicker = stringResource(R.string.downloads_kicker),
        title = stringResource(R.string.downloads_title),
        description = stringResource(R.string.downloads_description),
        modifier = modifier.padding(contentPadding),
    )
}

@Preview
@Composable
private fun DownloadsScreenPreview() {
    SBTheme { DownloadsScreen(PaddingValues()) }
}
