// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.component.expandedBy
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

private val CardHeight = 92.dp
private val CardGap = 10.dp

@Composable
fun TestsScreen(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding =
            contentPadding.expandedBy(
                horizontal = 16.dp,
                top = 24.dp,
                bottom = 24.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(CardGap),
        verticalArrangement = Arrangement.spacedBy(CardGap),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TestsHeader()
        }
        items(items = DeviceTest.entries, key = { it.name }) { test ->
            TestCard(label = stringResource(test.labelRes))
        }
    }
}

@Composable
private fun TestsHeader() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .pvotReveal(0)
                .padding(bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.tests_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
        )
        Text(
            text = stringResource(R.string.tests_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.tests_description),
            style = MaterialTheme.typography.bodyMedium,
            color = SBTheme.colors.muted,
        )
    }
}

@Composable
private fun TestCard(label: String) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(CardHeight)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(R.string.tests_not_run),
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.faint,
        )
    }
}
