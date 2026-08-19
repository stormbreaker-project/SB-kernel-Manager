// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.device

import android.content.Context

private const val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"

/**
 * The design capacity, which has no public API and whose sysfs node SELinux denies.
 *
 * Settings itself reads it from PowerProfile, so this asks the same class and returns
 * null when the hidden-API policy refuses rather than guessing a figure.
 */
internal object BatteryCapacity {
    fun designMah(context: Context): Int? =
        runCatching {
            val profileClass = Class.forName(POWER_PROFILE_CLASS)
            val profile = profileClass.getConstructor(Context::class.java).newInstance(context)
            val capacity = profileClass.getMethod("getBatteryCapacity").invoke(profile) as Double
            capacity.toInt().takeIf { it > 0 }
        }.getOrNull()
}
