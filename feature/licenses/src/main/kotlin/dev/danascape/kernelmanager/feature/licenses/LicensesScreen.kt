package dev.danascape.kernelmanager.feature.licenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.danascape.kernelmanager.feature.licenses.R
import dev.danascape.kernelmanager.core.designsystem.component.ActionRow
import dev.danascape.kernelmanager.core.designsystem.component.SettingsGroup
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.component.openArticle

@Composable
fun LicensesScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding.expandedBy(horizontal = 16.dp, top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.licenses_kicker),
                    style = MaterialTheme.typography.labelSmall,
                    color = SBTheme.colors.accent,
                )
                Text(
                    text = stringResource(R.string.licenses_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.licenses_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SBTheme.colors.muted,
                )
            }
        }

        item(key = "list") {
            SettingsGroup(title = stringResource(R.string.licenses_group)) {
                THIRD_PARTY_LICENSES.forEachIndexed { index, entry ->
                    ActionRow(
                        label = entry.name,
                        index = index,
                        count = THIRD_PARTY_LICENSES.size,
                        description = stringResource(R.string.licenses_entry_detail, entry.holder, entry.license),
                        onClick = { context.openArticle(entry.url, toolbarColor) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LicensesPreview() {
    SBTheme { LicensesScreen(PaddingValues()) }
}
