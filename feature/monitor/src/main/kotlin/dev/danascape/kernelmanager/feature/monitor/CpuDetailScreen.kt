// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.monitor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.component.MetricLine
import dev.danascape.kernelmanager.core.designsystem.component.SBSparkline
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.CpuCluster

@Composable
fun CpuDetailScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = viewModel(factory = MonitorViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.startSampling()
        onPauseOrDispose { viewModel.stopSampling() }
    }

    val topology = state.profile?.cpu

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            contentPadding.expandedBy(
                horizontal = 16.dp,
                top = 24.dp,
                bottom = 24.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(key = "header") {
            Column(
                modifier = Modifier.pvotReveal(0),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stringResource(R.string.monitor_cpu).lowercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = SBTheme.colors.accent,
                )
                Text(
                    text = stringResource(R.string.monitor_detail_cpu),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.monitor_detail_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SBTheme.colors.muted,
                )
            }
        }

        val perCore = state.history.perCoreLoad
        itemsIndexed(perCore) { core, values ->
            CoreChart(
                core = core,
                values = values,
                clusterFor = { topology?.clusters?.firstOrNull { core in it.cores } },
                currentKhz =
                    state.vitals
                        ?.cpu
                        ?.perCoreKhz
                        ?.getOrNull(core),
                revealIndex = core + 1,
            )
        }

        topology?.clusters?.forEach { cluster ->
            item(key = "cluster-${cluster.id}") { ClusterCard(cluster) }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.itemsIndexed(
    values: List<List<Float>>,
    content: @Composable (Int, List<Float>) -> Unit,
) {
    values.forEachIndexed { index, series ->
        item(key = "core-$index") { content(index, series) }
    }
}

@Composable
private fun CoreChart(
    core: Int,
    values: List<Float>,
    clusterFor: () -> CpuCluster?,
    currentKhz: Int?,
    revealIndex: Int,
) {
    val cluster = clusterFor()
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .pvotReveal(revealIndex)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.monitor_core, core),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text =
                    buildString {
                        append(values.lastOrNull()?.let { percent(it) } ?: "—")
                        currentKhz?.let { append(" · ${frequency(it)}") }
                    },
                style = MaterialTheme.typography.labelMedium,
                color = SBTheme.colors.muted,
            )
        }
        SBSparkline(values = values, domain = 0f..1f, height = 44.dp)
        cluster?.let {
            Text(
                text =
                    stringResource(R.string.monitor_cluster, it.id) +
                        " · " + (it.hardwareMaxKhz?.let(::frequency) ?: "—"),
                style = MaterialTheme.typography.labelSmall,
                color = SBTheme.colors.faint,
            )
        }
    }
}

@Composable
private fun ClusterCard(cluster: CpuCluster) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.monitor_cluster, cluster.id),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
        )
        MetricLine(
            stringResource(R.string.monitor_freq_range),
            "${cluster.minKhz?.let(::frequency) ?: "—"} – ${cluster.hardwareMaxKhz?.let(::frequency) ?: "—"}",
        )
        cluster.governor?.let { MetricLine(stringResource(R.string.monitor_governor), it) }
        if (cluster.availableGovernors.isNotEmpty()) {
            MetricLine(
                stringResource(R.string.monitor_governors_available),
                cluster.availableGovernors.joinToString(", "),
            )
        }
        if (cluster.availableKhz.isNotEmpty()) {
            MetricLine(
                stringResource(R.string.monitor_opp_count, cluster.availableKhz.size),
                cluster.availableKhz.joinToString(" ") { (it / 1000).toString() },
            )
        }
    }
}

@Preview
@Composable
private fun CpuDetailPreview() {
    SBTheme { CpuDetailScreen(PaddingValues()) }
}
