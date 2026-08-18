package dev.danascape.kernelmanager.feature.monitor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.danascape.kernelmanager.core.designsystem.component.SettingsGroup
import dev.danascape.kernelmanager.core.designsystem.component.ValueRow
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.component.topInset
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.BatteryVitals
import dev.danascape.kernelmanager.core.model.CpuCluster
import dev.danascape.kernelmanager.core.model.CpuTopology
import dev.danascape.kernelmanager.core.model.GpuInfo
import dev.danascape.kernelmanager.core.model.MemoryVitals
import dev.danascape.kernelmanager.core.model.SleepStats
import dev.danascape.kernelmanager.core.model.StorageVitals
import dev.danascape.kernelmanager.core.model.Vitals
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun MonitorScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = viewModel(factory = MonitorViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Sampling costs half a second of work per pass; it has no business
    // running while the user is looking at another screen.
    LifecycleResumeEffect(viewModel) {
        viewModel.startSampling()
        onPauseOrDispose { viewModel.stopSampling() }
    }

    MonitorContent(state = state, contentPadding = contentPadding, modifier = modifier)
}

@Composable
private fun MonitorContent(
    state: MonitorUiState,
    contentPadding: PaddingValues,
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

        state.profile?.cpu?.let { topology ->
            item(key = "cpu") { CpuGroup(topology, state.vitals) }
            topology.clusters.forEach { cluster ->
                item(key = "cluster-${cluster.id}") { ClusterGroup(cluster, state.vitals) }
            }
        }

        state.profile?.gpu?.let { gpu -> item(key = "gpu") { GpuGroup(gpu) } }
        state.vitals?.memory?.let { memory -> item(key = "memory") { MemoryGroup(memory) } }
        state.vitals?.storage?.let { storage -> item(key = "storage") { StorageGroup(storage) } }
        state.vitals?.battery?.let { battery -> item(key = "battery") { BatteryGroup(battery) } }
        state.vitals?.let { vitals ->
            item(key = "uptime") { UptimeGroup(vitals.sleep) }
            item(key = "network") { NetworkGroup(vitals) }
            item(key = "thermal") { ThermalGroup(vitals) }
        }
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.monitor_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
        )
        Text(
            text = stringResource(R.string.monitor_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.monitor_description),
            style = MaterialTheme.typography.bodyMedium,
            color = SBTheme.colors.muted,
        )
    }
}

@Composable
private fun CpuGroup(topology: CpuTopology, vitals: Vitals?) {
    val load = vitals?.load
    SettingsGroup(title = stringResource(R.string.monitor_cpu)) {
        val rows = buildList<@Composable (Int, Int) -> Unit> {
            load?.let { cpuLoad ->
                add { i, n ->
                    ValueRow(
                        stringResource(R.string.monitor_cpu_load),
                        stringResource(R.string.monitor_percent, (cpuLoad.average * 100).roundToInt()),
                        i, n,
                    )
                }
            }
            vitals?.cpu?.governor?.let { governor ->
                add { i, n -> ValueRow(stringResource(R.string.monitor_governor), governor, i, n) }
            }
            // Per-core load and frequency, which is what makes a heterogeneous
            // SoC legible: the little cores idle while one big core spikes.
            load?.perCore?.forEachIndexed { core, busy ->
                add { i, n ->
                    val khz = vitals?.cpu?.perCoreKhz?.getOrNull(core)
                    ValueRow(
                        label = stringResource(R.string.monitor_core, core),
                        value = stringResource(
                            R.string.monitor_core_value,
                            stringResource(R.string.monitor_percent, (busy * 100).roundToInt()),
                            khz?.let { formatFrequency(it) } ?: "—",
                        ),
                        index = i,
                        count = n,
                    )
                }
            }
        }
        rows.forEachIndexed { index, row -> row(index, rows.size) }
    }
}

@Composable
private fun ClusterGroup(cluster: CpuCluster, vitals: Vitals?) {
    SettingsGroup(title = stringResource(R.string.monitor_cluster, cluster.id)) {
        val rows = buildList<@Composable (Int, Int) -> Unit> {
            add { i, n ->
                ValueRow(
                    stringResource(R.string.monitor_cluster_cores, cluster.cores.size, cluster.partId ?: "—"),
                    cluster.cores.joinToString(", "),
                    i, n,
                )
            }
            add { i, n ->
                ValueRow(
                    stringResource(R.string.monitor_freq_range),
                    "${cluster.minKhz?.let { formatFrequency(it) } ?: "—"} – " +
                        (cluster.hardwareMaxKhz?.let { formatFrequency(it) } ?: "—"),
                    i, n,
                )
            }
            if (cluster.availableKhz.isNotEmpty()) {
                add { i, n ->
                    ValueRow(
                        stringResource(R.string.monitor_opp_count, cluster.availableKhz.size),
                        cluster.availableKhz.joinToString(" ") { (it / 1000).toString() } + " MHz",
                        i, n,
                    )
                }
            }
            cluster.governor?.let { governor ->
                add { i, n -> ValueRow(stringResource(R.string.monitor_governor), governor, i, n) }
            }
            if (cluster.availableGovernors.isNotEmpty()) {
                add { i, n ->
                    ValueRow(
                        stringResource(R.string.monitor_governors_available),
                        cluster.availableGovernors.joinToString(", "),
                        i, n,
                    )
                }
            }
        }
        rows.forEachIndexed { index, row -> row(index, rows.size) }
    }
}

@Composable
private fun GpuGroup(gpu: GpuInfo) {
    SettingsGroup(title = stringResource(R.string.monitor_gpu)) {
        val rows = listOfNotNull(
            gpu.renderer?.let { stringResource(R.string.monitor_gpu_renderer) to it },
            gpu.vendor?.let { stringResource(R.string.monitor_gpu_vendor) to it },
            gpu.glVersion?.let { stringResource(R.string.monitor_gpu_gl) to it },
        )
        rows.forEachIndexed { index, (label, value) ->
            ValueRow(label, value, index, rows.size)
        }
    }
    Text(
        text = stringResource(R.string.monitor_gpu_locked),
        style = MaterialTheme.typography.bodySmall,
        color = SBTheme.colors.faint,
        modifier = Modifier.padding(top = 10.dp, start = 4.dp, end = 4.dp),
    )
}

@Composable
private fun MemoryGroup(memory: MemoryVitals) {
    SettingsGroup(title = stringResource(R.string.monitor_memory)) {
        ValueRow(
            stringResource(R.string.monitor_used),
            "${formatBytes(memory.usedBytes)} · " +
                stringResource(R.string.monitor_percent, (memory.usedFraction * 100).roundToInt()),
            0, 2,
        )
        ValueRow(stringResource(R.string.monitor_total), formatBytes(memory.totalBytes), 1, 2)
    }
}

@Composable
private fun StorageGroup(storage: StorageVitals) {
    val count = if (storage.fileSystem != null) 3 else 2
    SettingsGroup(title = stringResource(R.string.monitor_storage)) {
        ValueRow(
            stringResource(R.string.monitor_used),
            "${formatBytes(storage.usedBytes)} · " +
                stringResource(R.string.monitor_percent, (storage.usedFraction * 100).roundToInt()),
            0, count,
        )
        ValueRow(stringResource(R.string.monitor_total), formatBytes(storage.totalBytes), 1, count)
        storage.fileSystem?.let {
            ValueRow(stringResource(R.string.monitor_filesystem), it, 2, count)
        }
    }
}

@Composable
private fun BatteryGroup(battery: BatteryVitals) {
    SettingsGroup(title = stringResource(R.string.monitor_battery)) {
        val rows = buildList<Pair<String, String>> {
            add(stringResource(R.string.monitor_level) to stringResource(R.string.monitor_percent, battery.percent))
            battery.temperatureC?.let {
                add(stringResource(R.string.monitor_temperature) to stringResource(R.string.monitor_celsius, it))
            }
            battery.currentMicroAmps?.let {
                // Sign convention varies by vendor, so show magnitude and let
                // the charging flag say the direction.
                add(stringResource(R.string.monitor_draw) to stringResource(R.string.monitor_milliamps, abs(it) / 1000))
            }
            battery.chargeCounterMicroAmpHours?.let {
                add(stringResource(R.string.monitor_capacity) to stringResource(R.string.monitor_milliamp_hours, it / 1000))
            }
            battery.health?.let { add(stringResource(R.string.monitor_health) to it) }
            battery.technology?.let { add(stringResource(R.string.monitor_technology) to it) }
        }
        rows.forEachIndexed { index, (label, value) -> ValueRow(label, value, index, rows.size) }
    }
}

@Composable
private fun UptimeGroup(sleep: SleepStats) {
    SettingsGroup(title = stringResource(R.string.monitor_uptime)) {
        ValueRow(stringResource(R.string.monitor_uptime), formatDuration(sleep.elapsedMillis), 0, 3)
        ValueRow(stringResource(R.string.monitor_awake), formatDuration(sleep.awakeMillis), 1, 3)
        ValueRow(
            stringResource(R.string.monitor_deep_sleep),
            "${formatDuration(sleep.deepSleepMillis)} · " +
                stringResource(R.string.monitor_percent, (sleep.deepSleepFraction * 100).roundToInt()),
            2, 3,
        )
    }
}

@Composable
private fun NetworkGroup(vitals: Vitals) {
    val network = vitals.network ?: return
    SettingsGroup(title = stringResource(R.string.monitor_network)) {
        ValueRow(stringResource(R.string.monitor_received), formatBytes(network.rxBytes), 0, 2)
        ValueRow(stringResource(R.string.monitor_sent), formatBytes(network.txBytes), 1, 2)
    }
}

@Composable
private fun ThermalGroup(vitals: Vitals) {
    SettingsGroup(title = stringResource(R.string.monitor_thermal)) {
        ValueRow(
            stringResource(R.string.monitor_throttling),
            vitals.thermal.name.lowercase().replaceFirstChar(Char::uppercase),
            0, 1,
        )
    }
}

private fun formatFrequency(khz: Int): String = if (khz >= 1_000_000) {
    String.format(Locale.getDefault(), "%.2f GHz", khz / 1_000_000f)
} else {
    String.format(Locale.getDefault(), "%d MHz", khz / 1000)
}

private fun formatBytes(bytes: Long): String {
    val gb = bytes / 1_073_741_824f
    return if (gb >= 1f) {
        String.format(Locale.getDefault(), "%.1f GB", gb)
    } else {
        String.format(Locale.getDefault(), "%.0f MB", bytes / 1_048_576f)
    }
}

private fun formatDuration(millis: Long): String {
    val totalMinutes = millis / 60_000
    val days = totalMinutes / 1440
    val hours = (totalMinutes % 1440) / 60
    val minutes = totalMinutes % 60
    return when {
        days > 0 -> "${days}d ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

@Preview
@Composable
private fun MonitorPreview() {
    SBTheme {
        MonitorContent(state = MonitorUiState(), contentPadding = PaddingValues())
    }
}
