package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prauga.pvot.designsystem.modifier.pvotPressScale
import com.prauga.pvot.designsystem.modifier.pvotReveal
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

/*
 * Grouped rows in the Material 3 expressive idiom: every row is its own filled
 * shape rather than a line in a bordered table. The group's outer corners are
 * generous and the corners where rows meet are tight, so a group reads as one
 * object while each row still reads as its own tappable thing.
 *
 * Fills instead of hairline outlines. An outlined table of identical rectangles
 * is a wireframe; a tonal fill gives each row weight.
 */

private val OuterRadius = 24.dp
private val InnerRadius = 6.dp
private val RowGap = 3.dp
private val MinRowHeight = 56.dp

/** Corners for a row at [index] of [count]: rounded outside, tight inside. */
fun groupShape(index: Int, count: Int): Shape {
    val top = if (index == 0) OuterRadius else InnerRadius
    val bottom = if (index == count - 1) OuterRadius else InnerRadius
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

@Composable
fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = SBTheme.colors.accent,
            // Leads the cascade its rows follow, so the group arrives as one.
            modifier = Modifier
                .then(pvotReveal(0))
                .padding(start = 4.dp),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(RowGap),
            content = content,
        )
    }
}

/**
 * One row of a group.
 *
 * Presses scale the row slightly on a spring rather than flashing a ripple —
 * the shape itself is the feedback, which is what keeps a list of these feeling
 * physical instead of tabular.
 */
@Composable
private fun GroupedRow(
    index: Int,
    count: Int,
    revealIndex: Int,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = groupShape(index, count)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(pvotReveal(revealIndex))
            .then(pvotPressScale(interactionSource))
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .heightIn(min = MinRowHeight)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

/** A label and a readout. Values sit in the mono channel. */
@Composable
fun ValueRow(
    label: String,
    value: String,
    index: Int,
    count: Int,
    revealIndex: Int = index,
) {
    GroupedRow(index = index, count = count, revealIndex = revealIndex, onClick = null) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = SBTheme.colors.muted,
            textAlign = TextAlign.End,
        )
    }
}

/**
 * A tappable row. [enabled] is false for things announced but not published,
 * which stay visible with their [trailing] note rather than disappearing — the
 * website lists them the same way.
 */
@Composable
fun ActionRow(
    label: String,
    index: Int,
    count: Int,
    description: String? = null,
    trailing: String? = null,
    enabled: Boolean = true,
    revealIndex: Int = index,
    onClick: () -> Unit,
) {
    GroupedRow(
        index = index,
        count = count,
        revealIndex = revealIndex,
        onClick = onClick.takeIf { enabled },
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else SBTheme.colors.faint,
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = SBTheme.colors.muted,
                )
            }
        }
        trailing?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = SBTheme.colors.muted,
            )
        }
    }
}
