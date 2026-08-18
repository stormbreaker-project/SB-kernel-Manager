package dev.danascape.kernelmanager.feature.devices

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun DevicesScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        kicker = "CATALOG",
        title = "Devices",
        description = "Every device Team StormBreaker builds for, with status and maintainer.",
        modifier = modifier,
    )
}

@Preview
@Composable
private fun DevicesScreenPreview() {
    SBTheme { DevicesScreen() }
}
