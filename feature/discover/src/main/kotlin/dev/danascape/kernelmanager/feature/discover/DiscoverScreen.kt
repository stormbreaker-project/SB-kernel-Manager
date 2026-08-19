// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prauga.pvot.designsystem.modifier.pvotPressScale
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.component.ActionRow
import dev.danascape.kernelmanager.core.designsystem.component.SettingsGroup
import dev.danascape.kernelmanager.core.designsystem.component.ValueRow
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.component.topInset
import dev.danascape.kernelmanager.core.designsystem.component.openArticle
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme
import dev.danascape.kernelmanager.core.model.BatteryVitals
import dev.danascape.kernelmanager.core.model.CpuVitals
import dev.danascape.kernelmanager.core.model.BootState
import dev.danascape.kernelmanager.core.model.CustomRom
import dev.danascape.kernelmanager.core.model.DeviceIdentity
import dev.danascape.kernelmanager.core.model.DeviceProfile
import dev.danascape.kernelmanager.core.model.NetworkVitals
import dev.danascape.kernelmanager.core.model.OsBuild
import dev.danascape.kernelmanager.core.model.SleepStats
import dev.danascape.kernelmanager.core.model.SocInfo
import dev.danascape.kernelmanager.core.model.StorageVitals
import dev.danascape.kernelmanager.core.model.ThermalStatus
import dev.danascape.kernelmanager.core.model.MemoryVitals
import dev.danascape.kernelmanager.core.model.NewsPost
import dev.danascape.kernelmanager.core.model.Vitals
import java.util.Locale

private const val KHZ_PER_GHZ = 1_000_000f
private const val BYTES_PER_GB = 1_073_741_824f

@Composable
fun DiscoverScreen(
    contentPadding: PaddingValues,
    onOpenDevice: () -> Unit,
    onOpenMonitoring: () -> Unit,
    onOpenNews: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = viewModel(factory = DiscoverViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    DiscoverContent(
        state = state,
        onOpenDevice = onOpenDevice,
        contentPadding = contentPadding,
        onOpenMonitoring = onOpenMonitoring,
        onOpenNews = onOpenNews,
        onHeadlineClick = { post -> context.openArticle(post.url, toolbarColor) },
        modifier = modifier,
    )
}

/** My device → what's happening → what's new. */
@Composable
private fun DiscoverContent(
    state: DiscoverUiState,
    contentPadding: PaddingValues,
    onOpenDevice: () -> Unit,
    onOpenMonitoring: () -> Unit,
    onOpenNews: () -> Unit,
    onHeadlineClick: (NewsPost) -> Unit,
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
        state.profile?.let { profile ->
            item(key = "identity") { IdentityCard(profile.identity, onOpenDevice) }
        }
        item(key = "update") { UpdateGroup() }
        item(key = "vitals") { VitalsGroup(state.vitals, onOpenMonitoring) }

        if (state.headlines.isNotEmpty()) {
            item(key = "news") {
                NewsGroup(state.headlines, onHeadlineClick = onHeadlineClick, onOpenNews = onOpenNews)
            }
        }
    }
}

@Composable
private fun IdentityCard(
    identity: DeviceIdentity,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .pvotReveal(0)
                .pvotPressScale(interactionSource)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.discover_kicker),
                style = MaterialTheme.typography.labelSmall,
                color = SBTheme.colors.accent,
            )
            Text(
                text = stringResource(R.string.discover_tap_detail),
                style = MaterialTheme.typography.labelSmall,
                color = SBTheme.colors.faint,
            )
        }
        Text(
            text = identity.displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text =
                stringResource(
                    R.string.discover_identity_meta,
                    identity.codename,
                    identity.androidRelease,
                    identity.sdkInt,
                ),
            style = MaterialTheme.typography.labelMedium,
            color = SBTheme.colors.muted,
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = SBTheme.colors.hairline,
        )

        Text(
            text = identity.kernelVersion,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (identity.kernelBuild.isNotEmpty()) {
            Text(
                text = identity.kernelBuild,
                style = MaterialTheme.typography.labelSmall,
                color = SBTheme.colors.faint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        StatusChip(identity, modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
private fun StatusChip(
    identity: DeviceIdentity,
    modifier: Modifier = Modifier,
) {
    val running = identity.isStormBreakerKernel
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(100.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (running) SBTheme.colors.accent else SBTheme.colors.faint),
        )
        Text(
            text =
                stringResource(
                    if (running) R.string.discover_kernel_running else R.string.discover_kernel_stock,
                ),
            style = MaterialTheme.typography.labelSmall,
            color = if (running) MaterialTheme.colorScheme.onSurface else SBTheme.colors.muted,
        )
    }
}

/** Deliberately a placeholder rather than a fake "up to date". */
@Composable
private fun UpdateGroup() {
    SettingsGroup(title = stringResource(R.string.discover_update)) {
        ActionRow(
            label = stringResource(R.string.discover_update_unavailable_title),
            index = 0,
            count = 1,
            description = stringResource(R.string.discover_update_unavailable_body),
            enabled = false,
            onClick = {},
        )
    }
}

@Composable
private fun VitalsGroup(
    vitals: Vitals?,
    onOpenMonitoring: () -> Unit,
) {
    SettingsGroup(title = stringResource(R.string.discover_vitals)) {
        val rows =
            buildList<@Composable (Int, Int) -> Unit> {
                vitals?.battery?.let { battery -> add { i, n -> BatteryRow(battery, i, n) } }
                vitals?.memory?.let { memory -> add { i, n -> MemoryRow(memory, i, n) } }
                vitals?.cpu?.let { cpu ->
                    add { i, n -> CpuRow(cpu, i, n) }
                    cpu.governor?.let { governor ->
                        add { i, n -> ValueRow(stringResource(R.string.discover_governor), governor, i, n) }
                    }
                }
                add { i, n ->
                    ActionRow(
                        label = stringResource(R.string.discover_view_monitoring),
                        index = i,
                        count = n,
                        onClick = onOpenMonitoring,
                    )
                }
            }
        rows.forEachIndexed { index, row -> row(index, rows.size) }
    }

    if (vitals != null && vitals.cpu?.temperatureC == null) {
        Text(
            text = stringResource(R.string.discover_thermal_locked),
            style = MaterialTheme.typography.bodySmall,
            color = SBTheme.colors.faint,
            modifier = Modifier.padding(top = 10.dp, start = 4.dp, end = 4.dp),
        )
    }
}

@Composable
private fun BatteryRow(
    battery: BatteryVitals,
    index: Int,
    count: Int,
) {
    val value =
        if (battery.charging) {
            stringResource(R.string.discover_battery_charging, battery.percent)
        } else {
            stringResource(R.string.discover_percent, battery.percent)
        }
    val withTemp =
        battery.temperatureC
            ?.let { "$value · " + stringResource(R.string.discover_celsius, it) }
            ?: value
    ValueRow(stringResource(R.string.discover_battery), withTemp, index, count)
}

@Composable
private fun MemoryRow(
    memory: MemoryVitals,
    index: Int,
    count: Int,
) {
    ValueRow(
        label = stringResource(R.string.discover_memory),
        value =
            stringResource(
                R.string.discover_memory_value,
                formatGigabytes(memory.usedBytes),
                formatGigabytes(memory.totalBytes),
            ),
        index = index,
        count = count,
    )
}

@Composable
private fun CpuRow(
    cpu: CpuVitals,
    index: Int,
    count: Int,
) {
    val current = cpu.perCoreKhz.maxOrNull()
    val max = cpu.maxKhz
    val value =
        when {
            current == null -> {
                "—"
            }

            max != null -> {
                stringResource(
                    R.string.discover_cpu_value,
                    stringResource(R.string.discover_ghz, current / KHZ_PER_GHZ),
                    stringResource(R.string.discover_ghz, max / KHZ_PER_GHZ),
                )
            }

            else -> {
                stringResource(R.string.discover_ghz, current / KHZ_PER_GHZ)
            }
        }
    ValueRow(stringResource(R.string.discover_cpu), value, index, count)
}

@Composable
private fun NewsGroup(
    headlines: List<NewsPost>,
    onHeadlineClick: (NewsPost) -> Unit,
    onOpenNews: () -> Unit,
) {
    SettingsGroup(title = stringResource(R.string.discover_news)) {
        val count = headlines.size + 1
        headlines.forEachIndexed { index, post ->
            ActionRow(
                label = post.title,
                index = index,
                count = count,
                description = post.tag,
                onClick = { onHeadlineClick(post) },
            )
        }
        ActionRow(
            label = stringResource(R.string.discover_news_all),
            index = count - 1,
            count = count,
            onClick = onOpenNews,
        )
    }
}

private fun formatGigabytes(bytes: Long): String = String.format(Locale.getDefault(), "%.1f GB", bytes / BYTES_PER_GB)

@Preview
@Composable
private fun DiscoverPreview() {
    val identity =
        DeviceIdentity(
            codename = "billie",
            model = "BE2029",
            manufacturer = "OnePlus",
            androidRelease = "14",
            sdkInt = 34,
            kernelRelease = "4.19.322-StormBreaker",
            isStormBreakerKernel = true,
        )
    SBTheme {
        DiscoverContent(
            state =
                DiscoverUiState(
                    profile =
                        DeviceProfile(
                            identity = identity,
                            os =
                                OsBuild(
                                    androidRelease = "14",
                                    sdkInt = 34,
                                    securityPatch = "2026-08-05",
                                    buildId = "UP1A.231005.007",
                                    fingerprint = null,
                                    tags = "release-keys",
                                    type = "user",
                                    rom = CustomRom("LineageOS", "21.0"),
                                ),
                            boot =
                                BootState(
                                    bootloaderUnlocked = true,
                                    verifiedBootState = "orange",
                                    encryption = "file",
                                ),
                            soc = SocInfo("lito", "qcom", "Qualcomm", "SM7225", listOf("arm64-v8a")),
                            cpu = null,
                            gpu = null,
                            suBinaryPresent = true,
                        ),
                    vitals =
                        Vitals(
                            cpu = CpuVitals(listOf(1_804_800, 820_000), 2_208_000, "schedutil", null),
                            load = null,
                            memory = MemoryVitals(6_600_000_000, 12_000_000_000),
                            battery =
                                BatteryVitals(
                                    72,
                                    31.4f,
                                    false,
                                    -420_000,
                                    3_100_000,
                                    "Good",
                                    "Li-ion",
                                    4438,
                                    3,
                                    5052,
                                ),
                            storage = StorageVitals(84_000_000_000, 128_000_000_000, "f2fs"),
                            network = NetworkVitals(1_200_000_000, 240_000_000),
                            thermal = ThermalStatus.NONE,
                            uptimeMillis = 9_000_000,
                            sleep = SleepStats(9_000_000, 3_400_000),
                        ),
                ),
            contentPadding = PaddingValues(),
            onOpenDevice = {},
            onOpenMonitoring = {},
            onOpenNews = {},
            onHeadlineClick = {},
        )
    }
}
