// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.devices

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.danascape.kernelmanager.feature.devices.R
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun DevicesScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    SectionPlaceholder(
        kicker = stringResource(R.string.devices_kicker),
        title = stringResource(R.string.devices_title),
        description = stringResource(R.string.devices_description),
        modifier = modifier.padding(contentPadding),
    )
}

@Preview
@Composable
private fun DevicesScreenPreview() {
    SBTheme { DevicesScreen(PaddingValues()) }
}
