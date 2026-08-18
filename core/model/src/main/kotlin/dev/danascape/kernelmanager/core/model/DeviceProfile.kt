// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.model

/** Everything about the device that does not change while the process is alive. */
data class DeviceProfile(
    val identity: DeviceIdentity,
    val os: OsBuild,
    val boot: BootState,
    val soc: SocInfo,
    val cpu: CpuTopology?,
    val gpu: GpuInfo?,
    val suBinaryPresent: Boolean,
)
