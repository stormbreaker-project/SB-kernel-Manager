// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.battery

import kotlinx.serialization.Serializable

/** One reading of the battery and the clocks, taken together. */
@Serializable
data class BatterySample(
    val elapsedMillis: Long,
    val awakeMillis: Long,
    val levelPercent: Int,
    val chargeMicroAmpHours: Int?,
    val currentMicroAmps: Int?,
    val voltageMilliVolts: Int?,
    val temperatureC: Float?,
    val charging: Boolean,
    val screenOn: Boolean,
)
