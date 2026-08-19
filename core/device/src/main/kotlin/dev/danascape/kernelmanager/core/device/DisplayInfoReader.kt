// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import android.content.Context
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import dev.danascape.kernelmanager.core.model.DisplayInfo
import kotlin.math.hypot

/** Panel geometry and refresh capability, all of it public API. */
class DisplayInfoReader(
    private val context: Context,
) {
    @Suppress("DEPRECATION")
    fun read(): DisplayInfo? {
        val manager = context.getSystemService(DisplayManager::class.java) ?: return null
        val display = manager.getDisplay(Display.DEFAULT_DISPLAY) ?: return null

        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)

        val diagonal =
            if (metrics.xdpi > 0f && metrics.ydpi > 0f) {
                hypot(metrics.widthPixels / metrics.xdpi, metrics.heightPixels / metrics.ydpi)
            } else {
                null
            }

        return DisplayInfo(
            widthPx = metrics.widthPixels,
            heightPx = metrics.heightPixels,
            densityDpi = metrics.densityDpi,
            xDpi = metrics.xdpi,
            yDpi = metrics.ydpi,
            refreshHz = display.refreshRate,
            supportedRefreshHz =
                display.supportedModes
                    .map { it.refreshRate }
                    .distinct()
                    .sorted(),
            hdrTypes = hdrTypes(display),
            diagonalInches = diagonal,
        )
    }

    private fun hdrTypes(display: Display): List<String> {
        val capabilities = display.hdrCapabilities ?: return emptyList()
        return capabilities.supportedHdrTypes.map { type ->
            when (type) {
                Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> "Dolby Vision"
                Display.HdrCapabilities.HDR_TYPE_HDR10 -> "HDR10"
                Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS -> "HDR10+"
                Display.HdrCapabilities.HDR_TYPE_HLG -> "HLG"
                else -> "Type $type"
            }
        }
    }
}
