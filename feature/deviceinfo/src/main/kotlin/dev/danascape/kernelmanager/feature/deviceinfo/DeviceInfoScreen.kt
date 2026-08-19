// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prauga.pvot.designsystem.modifier.pvotPressScale
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.component.ValueBlock
import dev.danascape.kernelmanager.core.designsystem.component.ValueRow
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.component.topInset
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.DeviceProfile

/** Longer than this and the value gets its own line rather than starving the label. */
private const val INLINE_VALUE_LIMIT = 28

/** A titled block of readouts, resolved once so the list can emit its rows lazily. */
internal data class InfoSection(
    val title: String,
    val rows: List<Pair<String, String>>,
)

/** The only tabs whose rows come from a live read. */
private val TABS_NEEDING_VITALS = setOf(DeviceInfoTab.MEMORY, DeviceInfoTab.BATTERY)

private val RowGap = 3.dp
private val TitleGap = 7.dp
private val GroupGap = 17.dp

@Composable
fun DeviceInfoScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: DeviceInfoViewModel = viewModel(factory = DeviceInfoViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DeviceInfoContent(
        state = state,
        contentPadding = contentPadding,
        onNeedVitals = viewModel::ensureVitals,
        modifier = modifier,
    )
}

@Composable
private fun DeviceInfoContent(
    state: DeviceInfoUiState,
    contentPadding: PaddingValues,
    onNeedVitals: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var tab by rememberSaveable { mutableStateOf(DeviceInfoTab.SYSTEM) }
    val listState = rememberLazyListState()
    val stripState = rememberLazyListState()
    val profile = state.profile

    val needVitals by rememberUpdatedState(onNeedVitals)
    LaunchedEffect(tab) {
        if (tab in TABS_NEEDING_VITALS) needVitals()
    }

    // A restored tab can sit off the end of the strip, and its rows start at the top.
    LaunchedEffect(tab) {
        stripState.animateScrollToItem(tab.ordinal)
        listState.scrollToItem(0)
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(top = contentPadding.topInset()),
    ) {
        if (profile == null) {
            Text(
                text = stringResource(R.string.device_info_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = SBTheme.colors.muted,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            )
            return@Column
        }

        HeaderCard(profile, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp))
        TabStrip(selected = tab, stripState = stripState, onSelect = { tab = it })

        val sections = tabSections(tab = tab, state = state, profile = profile)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                contentPadding.expandedBy(
                    horizontal = 16.dp,
                    bottom = 24.dp,
                    includeTopInset = false,
                ),
            verticalArrangement = Arrangement.spacedBy(RowGap),
        ) {
            sections.forEach { section ->
                item(key = "${section.title}#title") {
                    GroupTitle(section.title)
                }
                itemsIndexed(
                    items = section.rows,
                    key = { _, row -> "${section.title}#${row.first}" },
                ) { index, row ->
                    InfoRow(
                        label = row.first,
                        value = row.second,
                        index = index,
                        count = section.rows.size,
                    )
                }
            }
        }
    }
}

/** The section switcher. Scrollable because the labels will not fit at 360dp. */
@Composable
private fun TabStrip(
    selected: DeviceInfoTab,
    stripState: LazyListState,
    onSelect: (DeviceInfoTab) -> Unit,
) {
    LazyRow(
        state = stripState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(DeviceInfoTab.entries) { entry ->
            TabChip(
                label = stringResource(entry.labelRes),
                selected = entry == selected,
                onClick = { onSelect(entry) },
                revealIndex = entry.ordinal,
            )
        }
    }
}

@Composable
private fun TabChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    revealIndex: Int,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = if (selected) MaterialTheme.colorScheme.onPrimary else SBTheme.colors.muted,
        modifier =
            Modifier
                .pvotReveal(revealIndex)
                .pvotPressScale(interactionSource)
                .clip(RoundedCornerShape(100.dp))
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                ).clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ).padding(horizontal = 14.dp, vertical = 9.dp),
    )
}

@Composable
private fun HeaderCard(
    profile: DeviceProfile,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .pvotReveal(0)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.device_info_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
        )
        Text(
            text = profile.identity.displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = profile.identity.codename,
            style = MaterialTheme.typography.labelMedium,
            color = SBTheme.colors.muted,
        )
    }
}

/** The group heading, matching SettingsGroup's but emitted as its own list item. */
@Composable
internal fun GroupTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = SBTheme.colors.accent,
        modifier = Modifier.padding(start = 4.dp, top = GroupGap, bottom = TitleGap),
    )
}

/** One readout, stacked when the value is too long to sit beside its label. */
@Composable
internal fun InfoRow(
    label: String,
    value: String,
    index: Int,
    count: Int,
) {
    if (value.length > INLINE_VALUE_LIMIT || '\n' in value) {
        ValueBlock(label = label, value = value, index = index, count = count)
    } else {
        ValueRow(label = label, value = value, index = index, count = count)
    }
}

/** Drops rows whose value is null or blank, so nothing renders as an empty readout. */
internal fun buildRows(vararg rows: Pair<String, String?>): List<Pair<String, String>> =
    rows.mapNotNull { (label, value) ->
        value?.takeIf { it.isNotBlank() }?.let { label to it }
    }
