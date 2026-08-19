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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.component.ValueBlock
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

private val SensorRowGap = 3.dp

@Composable
fun SensorsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: SensorsViewModel = viewModel(factory = SensorsViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SensorsContent(state = state, contentPadding = contentPadding, modifier = modifier)
}

@Composable
private fun SensorsContent(
    state: SensorsUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val sensors = state.sensors

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            contentPadding.expandedBy(
                horizontal = 16.dp,
                top = 24.dp,
                bottom = 24.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(SensorRowGap),
    ) {
        item(key = "header") {
            SensorHeader(count = sensors.size, deviceName = state.deviceName)
        }
        if (sensors.isNotEmpty()) {
            item(key = "title") {
                GroupTitle(stringResource(R.string.sensors_available))
            }
            itemsIndexed(items = sensors, key = { _, sensor -> sensor.name }) { index, sensor ->
                ValueBlock(
                    label = sensor.name,
                    value = stringResource(R.string.sensors_subtitle, sensor.type, sensor.vendor),
                    index = index,
                    count = sensors.size,
                    revealIndex = null,
                )
            }
        }
    }
}

@Composable
private fun SensorHeader(
    count: Int,
    deviceName: String?,
) {
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
            text = stringResource(R.string.sensors_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
        )
        Text(
            text = pluralStringResource(R.plurals.sensors_count, count, count),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        deviceName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = SBTheme.colors.muted,
            )
        }
    }
}
