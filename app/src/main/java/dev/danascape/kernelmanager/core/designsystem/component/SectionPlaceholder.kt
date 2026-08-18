package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

/**
 * Stand-in for a destination whose content has not been built yet.
 */
@Composable
fun SectionPlaceholder(
    kicker: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = kicker,
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.faint,
            textAlign = TextAlign.Center,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = SBTheme.colors.muted,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun SectionPlaceholderPreview() {
    SBTheme {
        SectionPlaceholder(
            kicker = "PHASE 0",
            title = "Devices",
            description = "Every device Team StormBreaker builds for, with status and maintainer.",
        )
    }
}
