// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val LineWidth = 2.dp
private val EndDotRadius = 4.dp
private val SurfaceRing = 2.dp

/**
 * A single series over time, drawn without axes or labels.
 *
 * [domain] is fixed rather than fitted to the data on purpose. Auto-scaling a
 * metric that moved between 11% and 13% fills the whole height and reads as a
 * dramatic swing, which is the most common way a sparkline lies. Pass null only
 * for series with no natural range.
 */
@Composable
fun SBSparkline(
    values: List<Float>,
    modifier: Modifier = Modifier,
    domain: ClosedFloatingPointRange<Float>? = 0f..1f,
    color: Color = MaterialTheme.colorScheme.primary,
    surface: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    height: Dp = 40.dp,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        if (values.size < 2) return@Canvas

        val low = domain?.start ?: values.min()
        val high = domain?.endInclusive ?: values.max()
        val span = (high - low).takeIf { it > 0f } ?: 1f

        val stepX = size.width / (values.size - 1)
        val points = values.mapIndexed { index, value ->
            val fraction = ((value - low) / span).coerceIn(0f, 1f)
            Offset(x = index * stepX, y = size.height * (1f - fraction))
        }

        val line = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }

        // A wash, never a saturated block.
        val fill = Path().apply {
            addPath(line)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }
        drawPath(
            path = fill,
            brush = Brush.verticalGradient(
                listOf(color.copy(alpha = 0.14f), color.copy(alpha = 0.02f)),
            ),
        )

        drawPath(
            path = line,
            color = color,
            style = Stroke(
                width = LineWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )

        // The end dot marks "now"; the ring keeps it legible over the line.
        val last = points.last()
        drawCircle(color = surface, radius = (EndDotRadius + SurfaceRing).toPx(), center = last)
        drawCircle(color = color, radius = EndDotRadius.toPx(), center = last)
    }
}
