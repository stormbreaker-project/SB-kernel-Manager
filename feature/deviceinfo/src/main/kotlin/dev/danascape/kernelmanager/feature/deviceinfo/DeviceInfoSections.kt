// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.danascape.kernelmanager.core.model.DeviceDetails
import dev.danascape.kernelmanager.core.model.DeviceProfile
import dev.danascape.kernelmanager.core.model.Vitals
import java.util.Locale

@Composable
internal fun tabSections(
    tab: DeviceInfoTab,
    state: DeviceInfoUiState,
    profile: DeviceProfile,
): List<InfoSection> {
    val sections =
        when (tab) {
            DeviceInfoTab.SYSTEM -> listOf(systemSection(profile, state.details))
            DeviceInfoTab.SOC -> listOf(socSection(profile), gpuSection(profile))
            DeviceInfoTab.SCREEN -> listOf(screenSection(state.details))
            DeviceInfoTab.MEMORY -> listOf(memorySection(state.details), storageSection(state.vitals))
            DeviceInfoTab.BATTERY -> listOf(batterySection(state.vitals))
            DeviceInfoTab.CAMERAS -> cameraSections(state.details)
            DeviceInfoTab.CODECS -> listOf(codecSection(state.details))
            DeviceInfoTab.BOOT -> listOf(bootSection(profile))
        }
    return sections.filter { it.rows.isNotEmpty() }
}

@Composable
private fun systemSection(
    profile: DeviceProfile,
    details: DeviceDetails?,
): InfoSection {
    val os = profile.os
    val build = details?.build
    return InfoSection(
        title = stringResource(R.string.device_info_section_system),
        rows =
            buildRows(
                stringResource(R.string.device_info_manufacturer) to profile.identity.manufacturer,
                stringResource(R.string.device_info_brand) to build?.brand,
                stringResource(R.string.device_info_model) to profile.identity.model,
                stringResource(R.string.device_info_device) to build?.device,
                stringResource(R.string.device_info_product) to build?.product,
                stringResource(R.string.device_info_board) to build?.board,
                stringResource(R.string.device_info_release) to os.androidRelease,
                stringResource(R.string.device_info_api) to os.sdkInt.toString(),
                stringResource(R.string.device_info_build) to os.buildId,
                stringResource(R.string.device_info_incremental) to build?.incremental,
                stringResource(R.string.device_info_build_type) to os.type,
                stringResource(R.string.device_info_tags) to os.tags,
                stringResource(R.string.device_info_security_patch) to os.securityPatch,
                stringResource(R.string.device_info_build_date) to build?.buildTimeMillis?.asBuildDate(),
                stringResource(R.string.device_info_build_host) to build?.host,
                stringResource(R.string.device_info_java_vm) to build?.javaVm,
                stringResource(R.string.device_info_bootloader) to build?.bootloader,
                stringResource(R.string.device_info_baseband) to build?.radio,
                stringResource(R.string.device_info_rom) to os.rom?.let { "${it.name} ${it.version}" },
                stringResource(R.string.device_info_kernel) to profile.identity.kernelRelease,
                stringResource(R.string.device_info_kernel_full) to build?.kernelFull,
                stringResource(R.string.device_info_fingerprint) to os.fingerprint,
            ),
    )
}

@Composable
private fun socSection(profile: DeviceProfile): InfoSection {
    val soc = profile.soc
    val cpu = profile.cpu
    val clusterTemplate = stringResource(R.string.device_info_cluster_value)
    val clusters =
        cpu?.clusters?.joinToString("\n") { cluster ->
            String.format(
                Locale.US,
                clusterTemplate,
                cluster.cores.size,
                cluster.hardwareMaxKhz?.khzAsGhz() ?: cluster.maxKhz?.khzAsGhz().orEmpty(),
            )
        }
    return InfoSection(
        title = stringResource(R.string.device_info_section_soc),
        rows =
            buildRows(
                stringResource(R.string.device_info_soc_model) to soc.model,
                stringResource(R.string.device_info_soc_vendor) to soc.manufacturer,
                stringResource(R.string.device_info_platform) to soc.platform,
                stringResource(R.string.device_info_hardware) to soc.hardware,
                stringResource(R.string.device_info_cores) to cpu?.coreCount?.toString(),
                stringResource(R.string.device_info_clusters) to clusters,
                stringResource(R.string.device_info_governor) to cpu?.clusters?.firstNotNullOfOrNull { it.governor },
                stringResource(R.string.device_info_abis) to soc.supportedAbis.joinToString(" "),
                stringResource(R.string.device_info_instructions) to cpu?.features?.joinToString(" "),
            ),
    )
}

@Composable
private fun gpuSection(profile: DeviceProfile): InfoSection {
    val gpu = profile.gpu
    return InfoSection(
        title = stringResource(R.string.device_info_section_gpu),
        rows =
            buildRows(
                stringResource(R.string.device_info_renderer) to gpu?.renderer,
                stringResource(R.string.device_info_soc_vendor) to gpu?.vendor,
                stringResource(R.string.device_info_gl_version) to gpu?.glVersion,
            ),
    )
}

@Composable
private fun screenSection(details: DeviceDetails?): InfoSection {
    val display = details?.display
    return InfoSection(
        title = stringResource(R.string.device_info_section_screen),
        rows =
            buildRows(
                stringResource(R.string.device_info_resolution) to
                    display?.let { stringResource(R.string.device_info_pixels, it.widthPx, it.heightPx) },
                stringResource(R.string.device_info_aspect) to display?.aspectRatio,
                stringResource(R.string.device_info_density) to
                    display?.let { stringResource(R.string.device_info_dpi_value, it.densityDpi) },
                stringResource(R.string.device_info_dpi) to
                    display?.let {
                        stringResource(
                            R.string.device_info_dpi_physical,
                            it.xDpi.decimals(0),
                            it.yDpi.decimals(0),
                        )
                    },
                stringResource(R.string.device_info_diagonal) to
                    display?.diagonalInches?.let { stringResource(R.string.device_info_inches, it.decimals(1)) },
                stringResource(R.string.device_info_refresh) to
                    display?.let { stringResource(R.string.device_info_hertz, it.refreshHz.decimals(0)) },
                stringResource(R.string.device_info_refresh_modes) to
                    display?.supportedRefreshHz?.joinDistinct(places = 0, suffix = " Hz"),
                stringResource(R.string.device_info_hdr) to display?.hdrTypes?.joinToString(", "),
            ),
    )
}

@Composable
private fun memorySection(details: DeviceDetails?): InfoSection {
    val memory = details?.memory
    return InfoSection(
        title = stringResource(R.string.device_info_section_memory),
        rows =
            buildRows(
                stringResource(R.string.device_info_ram_total) to memory?.totalBytes?.asByteSize(),
                stringResource(R.string.device_info_ram_available) to memory?.availableBytes?.asByteSize(),
                stringResource(R.string.device_info_ram_threshold) to memory?.thresholdBytes?.asByteSize(),
                stringResource(R.string.device_info_swap) to memory?.swapTotalBytes?.takeIf { it > 0 }?.asByteSize(),
                stringResource(R.string.device_info_page_size) to memory?.pageSizeBytes?.asByteSize(),
            ),
    )
}

@Composable
private fun storageSection(vitals: Vitals?): InfoSection {
    val storage = vitals?.storage
    return InfoSection(
        title = stringResource(R.string.device_info_section_storage),
        rows =
            buildRows(
                stringResource(R.string.device_info_storage_used) to storage?.usedBytes?.asByteSize(),
                stringResource(R.string.device_info_storage_total) to storage?.totalBytes?.asByteSize(),
                stringResource(R.string.device_info_filesystem) to storage?.fileSystem,
            ),
    )
}

@Composable
private fun batterySection(vitals: Vitals?): InfoSection {
    val battery = vitals?.battery
    return InfoSection(
        title = stringResource(R.string.device_info_section_battery),
        rows =
            buildRows(
                stringResource(R.string.device_info_level) to
                    battery?.let { stringResource(R.string.device_info_percent, it.percent) },
                stringResource(R.string.device_info_health) to battery?.health,
                stringResource(R.string.device_info_technology) to battery?.technology,
                stringResource(R.string.device_info_temperature) to
                    battery?.temperatureC?.let { stringResource(R.string.device_info_celsius, it) },
                stringResource(R.string.device_info_current) to
                    battery?.currentMicroAmps?.let { stringResource(R.string.device_info_milliamps, it / 1000) },
                stringResource(R.string.device_info_voltage) to
                    battery?.voltageMillivolts?.let {
                        stringResource(R.string.device_info_volts, (it / 1000f).decimals(3))
                    },
                stringResource(R.string.device_info_design_capacity) to
                    battery?.designCapacityMah?.let { stringResource(R.string.device_info_milliamp_hours, it) },
                stringResource(R.string.device_info_charge_remaining) to
                    battery?.chargeCounterMicroAmpHours?.let {
                        stringResource(R.string.device_info_milliamp_hours, it / 1000)
                    },
                stringResource(R.string.device_info_cycle_count) to battery?.cycleCount?.toString(),
            ),
    )
}

@Composable
private fun cameraSections(details: DeviceDetails?): List<InfoSection> =
    details?.cameras.orEmpty().map { camera ->
        InfoSection(
            title = stringResource(R.string.device_info_section_camera, camera.id, camera.facing),
            rows =
                buildRows(
                    stringResource(R.string.device_info_cam_resolution) to
                        stringResource(
                            R.string.device_info_megapixels,
                            camera.megapixels.decimals(1),
                            camera.widthPx,
                            camera.heightPx,
                        ),
                    stringResource(R.string.device_info_cam_aperture) to
                        camera.apertures
                            .takeIf { it.isNotEmpty() }
                            ?.distinct()
                            ?.sorted()
                            ?.joinToString(", ") { "f/${it.decimals(1)}" },
                    stringResource(R.string.device_info_cam_focal) to
                        camera.focalLengths.takeIf { it.isNotEmpty() }?.joinDistinct(places = 2, suffix = " mm"),
                    stringResource(R.string.device_info_cam_sensor_size) to
                        camera.sensorWidthMm?.let { width ->
                            camera.sensorHeightMm?.let { height ->
                                "${width.decimals(2)} × ${height.decimals(2)} mm"
                            }
                        },
                    stringResource(R.string.device_info_cam_pixel_size) to
                        camera.pixelSizeMicrons?.let {
                            stringResource(R.string.device_info_microns, it.decimals(2))
                        },
                    stringResource(R.string.device_info_cam_iso) to
                        camera.isoMin?.let { min ->
                            camera.isoMax?.let { max -> stringResource(R.string.device_info_iso_range, min, max) }
                        },
                    stringResource(R.string.device_info_cam_zoom) to
                        camera.maxDigitalZoom?.let { stringResource(R.string.device_info_zoom, it.decimals(1)) },
                    stringResource(R.string.device_info_cam_level) to camera.hardwareLevel,
                    stringResource(R.string.device_info_cam_flash) to
                        stringResource(if (camera.hasFlash) R.string.device_info_yes else R.string.device_info_no),
                ),
        )
    }

@Composable
private fun codecSection(details: DeviceDetails?): InfoSection {
    val codecs = details?.codecs
    return InfoSection(
        title = stringResource(R.string.device_info_section_codecs),
        rows =
            buildRows(
                stringResource(R.string.device_info_decoders) to codecs?.decoders?.toString(),
                stringResource(R.string.device_info_encoders) to codecs?.encoders?.toString(),
                stringResource(R.string.device_info_hw_accelerated) to codecs?.hardwareAccelerated?.toString(),
                stringResource(R.string.device_info_formats) to codecs?.notableFormats?.joinToString(", "),
            ),
    )
}

@Composable
private fun bootSection(profile: DeviceProfile): InfoSection {
    val boot = profile.boot
    return InfoSection(
        title = stringResource(R.string.device_info_section_boot),
        rows =
            buildRows(
                stringResource(R.string.device_info_bootloader_state) to
                    boot.bootloaderUnlocked?.let {
                        stringResource(if (it) R.string.device_info_unlocked else R.string.device_info_locked)
                    },
                stringResource(R.string.device_info_verified_boot) to boot.verifiedBootState,
                stringResource(R.string.device_info_encryption) to boot.encryption,
                stringResource(R.string.device_info_su) to
                    stringResource(
                        if (profile.suBinaryPresent) R.string.device_info_present else R.string.device_info_absent,
                    ),
            ),
    )
}
