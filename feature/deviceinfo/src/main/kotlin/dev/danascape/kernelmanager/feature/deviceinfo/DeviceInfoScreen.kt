// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun DeviceInfoScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SectionPlaceholder(
        kicker = stringResource(R.string.device_info_kicker),
        title = stringResource(R.string.device_info_title),
        description = stringResource(R.string.device_info_description),
        modifier = modifier.padding(contentPadding),
    )
}

@Preview
@Composable
private fun DeviceInfoScreenPreview() {
    SBTheme { DeviceInfoScreen(PaddingValues()) }
}
