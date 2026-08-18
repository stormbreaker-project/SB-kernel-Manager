package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import dev.danascape.kernelmanager.core.designsystem.theme.SBMotion

/**
 * Springs a surface down slightly while pressed.
 *
 * Used instead of a ripple on the large filled surfaces — cards and grouped
 * rows — where the shape itself is the affordance. A ripple on a 24dp-radius
 * card reads as a flash on top of the design; scaling reads as touching it.
 */
@Composable
fun pressScaleModifier(interactionSource: InteractionSource): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) SBMotion.PressedScale else 1f,
        animationSpec = SBMotion.pressSpring(),
        label = "pressScale",
    )
    return Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
