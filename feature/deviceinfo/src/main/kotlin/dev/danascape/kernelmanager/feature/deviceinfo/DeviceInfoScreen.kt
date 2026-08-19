// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.component.SettingsGroup
import dev.danascape.kernelmanager.core.designsystem.component.ValueBlock
import dev.danascape.kernelmanager.core.designsystem.component.ValueRow
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.component.topInset
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.DeviceProfile

/** Longer than this and the value gets its own line rather than starving the label. */
private const val INLINE_VALUE_LIMIT = 28

@Composable
fun DeviceInfoScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: DeviceInfoViewModel = viewModel(factory = DeviceInfoViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DeviceInfoContent(state = state, contentPadding = contentPadding, modifier = modifier)
}

@Composable
private fun DeviceInfoContent(
    state: DeviceInfoUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(top = contentPadding.topInset()),
        contentPadding =
            contentPadding.expandedBy(
                horizontal = 16.dp,
                top = 24.dp,
                bottom = 24.dp,
                includeTopInset = false,
            ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        val profile = state.profile
        if (profile == null) {
            item(key = "loading") {
                Text(
                    text = stringResource(R.string.device_info_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SBTheme.colors.muted,
                )
            }
            return@LazyColumn
        }

        item(key = "header") { HeaderCard(profile) }
        item(key = "system") { SystemGroup(profile, state.details) }
        item(key = "soc") { SocGroup(profile) }
        item(key = "gpu") { GpuGroup(profile) }
        item(key = "screen") { ScreenGroup(state.details) }
        item(key = "memory") { MemoryGroup(state.details) }
        item(key = "storage") { StorageGroup(state.vitals) }
        item(key = "battery") { BatteryGroup(state.vitals) }
        item(key = "cameras") { CameraGroups(state.details) }
        item(key = "codecs") { CodecGroup(state.details) }
        item(key = "boot") { BootGroup(profile) }
        item(key = "sensors") { SensorGroups(state.details) }
    }
}

@Composable
private fun HeaderCard(profile: DeviceProfile) {
    Column(
        modifier =
            Modifier
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

/** A titled group, skipped entirely when nothing under it could be read. */
@Composable
internal fun InfoGroup(
    title: String,
    rows: List<Pair<String, String>>,
) {
    if (rows.isEmpty()) return
    SettingsGroup(title = title) {
        rows.forEachIndexed { index, (label, value) ->
            if (value.length > INLINE_VALUE_LIMIT || '\n' in value) {
                ValueBlock(
                    label = label,
                    value = value,
                    index = index,
                    count = rows.size,
                )
            } else {
                ValueRow(
                    label = label,
                    value = value,
                    index = index,
                    count = rows.size,
                )
            }
        }
    }
}

/** Drops rows whose value is null or blank, so nothing renders as an empty readout. */
internal fun buildRows(vararg rows: Pair<String, String?>): List<Pair<String, String>> =
    rows.mapNotNull { (label, value) ->
        value?.takeIf { it.isNotBlank() }?.let { label to it }
    }
