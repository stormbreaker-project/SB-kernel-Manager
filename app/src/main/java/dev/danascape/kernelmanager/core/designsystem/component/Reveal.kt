package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import dev.danascape.kernelmanager.core.designsystem.theme.SBMotion

/**
 * Fades and lifts an element into place, staggered by [index].
 *
 * The app-side equivalent of the website's `.reveal` / `data-stagger` pair:
 * same settle curve, same idea of a per-item delay that stops growing after a
 * few items so a long list never feels like it is waiting on an animation.
 */
@Composable
fun revealModifier(index: Int): Modifier {
    val rise = with(LocalDensity.current) { SBMotion.RevealRise.toPx() }
    var appeared by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(
            durationMillis = SBMotion.RevealDurationMillis,
            delayMillis = index.coerceAtMost(SBMotion.MaxStaggeredItems) * SBMotion.StaggerStepMillis,
            easing = SBMotion.Settle,
        ),
        label = "reveal",
    )

    LaunchedEffect(Unit) { appeared = true }

    return Modifier.graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * rise
    }
}
