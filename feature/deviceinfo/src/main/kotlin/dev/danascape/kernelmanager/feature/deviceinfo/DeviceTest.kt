// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.annotation.StringRes

/** A hardware check the user runs by hand. None of them are wired up yet. */
enum class DeviceTest(
    @param:StringRes val labelRes: Int,
) {
    DISPLAY(R.string.test_display),
    TOUCH(R.string.test_touch),
    MULTITOUCH(R.string.test_multitouch),
    FLASHLIGHT(R.string.test_flashlight),
    VIBRATION(R.string.test_vibration),
    SPEAKER(R.string.test_speaker),
    EARPIECE(R.string.test_earpiece),
    MICROPHONE(R.string.test_microphone),
    VOLUME_UP(R.string.test_volume_up),
    VOLUME_DOWN(R.string.test_volume_down),
    PROXIMITY(R.string.test_proximity),
    LIGHT_SENSOR(R.string.test_light_sensor),
    ACCELEROMETER(R.string.test_accelerometer),
    GYROSCOPE(R.string.test_gyroscope),
    FINGERPRINT(R.string.test_fingerprint),
    BATTERY(R.string.test_battery),
}
