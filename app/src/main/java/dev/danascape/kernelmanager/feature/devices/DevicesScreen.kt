package dev.danascape.kernelmanager.feature.devices

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun DevicesScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SectionPlaceholder(
        kicker = "CATALOG",
        title = "Devices",
        description = "Every device Team StormBreaker builds for, with status and maintainer.",
        modifier = modifier.padding(contentPadding),
    )
}

@Preview
@Composable
private fun DevicesScreenPreview() {
    SBTheme { DevicesScreen(PaddingValues()) }
}
