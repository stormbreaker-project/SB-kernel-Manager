package dev.danascape.kernelmanager.feature.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.pm.PackageInfoCompat
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.danascape.kernelmanager.feature.more.R
import dev.danascape.kernelmanager.core.model.ThemePreference
import dev.danascape.kernelmanager.core.designsystem.component.ActionRow
import dev.danascape.kernelmanager.core.designsystem.component.SettingsGroup
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.component.ValueRow
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.LinkItem
import dev.danascape.kernelmanager.core.model.LinkSection
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.component.topInset
import dev.danascape.kernelmanager.core.designsystem.component.openArticle

@Composable
fun MoreScreen(
    contentPadding: PaddingValues,
    onOpenDevices: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoreViewModel = viewModel(factory = MoreViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    MoreContent(
        state = state,
        contentPadding = contentPadding,
        onLinkClick = { item -> item.url?.let { context.openArticle(it, toolbarColor) } },
        onThemeChange = viewModel::setTheme,
        onOpenDevices = onOpenDevices,
        onOpenNews = onOpenNews,
        onOpenLicenses = onOpenLicenses,
        modifier = modifier,
    )
}

@Composable
private fun MoreContent(
    state: MoreUiState,
    contentPadding: PaddingValues,
    onLinkClick: (LinkItem) -> Unit,
    onThemeChange: (ThemePreference) -> Unit,
    onOpenDevices: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenLicenses: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = contentPadding.topInset()),
        contentPadding = contentPadding.expandedBy(
            horizontal = 16.dp,
            top = 24.dp,
            bottom = 24.dp,
            includeTopInset = false,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item(key = "header") { Header() }

        item(key = "browse") {
            BrowseGroup(onOpenDevices = onOpenDevices, onOpenNews = onOpenNews)
        }

        item(key = "appearance") {
            SettingsGroup(title = stringResource(R.string.more_appearance)) {
                ThemeRow(selected = state.theme, onSelect = onThemeChange)
            }
        }

        items(state.sections.size, key = { state.sections[it].id }) { index ->
            LinkGroup(section = state.sections[index], onLinkClick = onLinkClick)
        }

        item(key = "about") { AboutGroup(onOpenLicenses = onOpenLicenses) }
    }
}

/** Destinations that lost their tab in favour of the kernel-facing ones. */
@Composable
private fun BrowseGroup(onOpenDevices: () -> Unit, onOpenNews: () -> Unit) {
    SettingsGroup(title = stringResource(R.string.more_browse)) {
        ActionRow(
            label = stringResource(R.string.more_browse_devices),
            index = 0,
            count = 2,
            description = stringResource(R.string.more_browse_devices_description),
            onClick = onOpenDevices,
        )
        ActionRow(
            label = stringResource(R.string.more_browse_news),
            index = 1,
            count = 2,
            description = stringResource(R.string.more_browse_news_description),
            onClick = onOpenNews,
        )
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.then(pvotReveal(0)),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.more_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
        )
        Text(
            text = stringResource(R.string.more_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun LinkGroup(section: LinkSection, onLinkClick: (LinkItem) -> Unit) {
    SettingsGroup(title = section.title) {
        section.items.forEachIndexed { index, item ->
            ActionRow(
                label = item.label,
                index = index,
                count = section.items.size,
                description = item.description,
                // Only "soon" earns a trailing note; it is the one case where
                // tapping does nothing and the row needs to explain itself.
                trailing = stringResource(R.string.more_soon).takeIf { item.soon },
                enabled = item.openable,
                onClick = { onLinkClick(item) },
            )
        }
    }
}

@Composable
private fun ThemeRow(selected: ThemePreference, onSelect: (ThemePreference) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        ActionRow(
            label = stringResource(R.string.more_theme),
            index = 0,
            count = 1,
            trailing = stringResource(selected.labelRes()),
            onClick = { expanded = true },
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ThemePreference.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(option.labelRes()),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (option == selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AboutGroup(onOpenLicenses: () -> Unit) {
    SettingsGroup(title = stringResource(R.string.more_about)) {
        ValueRow(
            label = stringResource(R.string.more_about_version),
            value = rememberAppVersion(),
            index = 0,
            count = 2,
        )
        ActionRow(
            label = stringResource(R.string.more_about_licenses),
            index = 1,
            count = 2,
            description = stringResource(R.string.more_about_licenses_description),
            onClick = onOpenLicenses,
        )
    }
}

/** Read from the installed package, so a feature module needs no app BuildConfig. */
@Composable
private fun rememberAppVersion(): String {
    val context = LocalContext.current
    val info = remember(context) { context.packageManager.getPackageInfo(context.packageName, 0) }
    return stringResource(
        R.string.more_about_version_format,
        info.versionName.orEmpty(),
        PackageInfoCompat.getLongVersionCode(info),
    )
}

private fun ThemePreference.labelRes(): Int = when (this) {
    ThemePreference.SYSTEM -> R.string.more_theme_system
    ThemePreference.LIGHT -> R.string.more_theme_light
    ThemePreference.DARK -> R.string.more_theme_dark
}

@Preview
@Composable
private fun MorePreview() {
    SBTheme {
        MoreContent(
            state = MoreUiState(),
            contentPadding = PaddingValues(),
            onLinkClick = {},
            onThemeChange = {},
            onOpenDevices = {},
            onOpenNews = {},
            onOpenLicenses = {},
        )
    }
}
