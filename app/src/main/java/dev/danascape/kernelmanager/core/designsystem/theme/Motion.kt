package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.dp

/**
 * Motion tokens, taken from the website rather than invented.
 *
 * base.css settles its reveals on `cubic-bezier(.16, 1, .3, 1)` — an expo-out
 * curve that arrives fast and eases to rest — and staggers grouped items by a
 * fixed step. Reusing both is what makes the app feel like the same product in
 * motion, not just in colour.
 */
object SBMotion {

    /** The site's settle curve. */
    val Settle = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

    const val RevealDurationMillis = 420

    /** base.css uses 75ms; tighter here because app lists are denser than pages. */
    const val StaggerStepMillis = 45

    /** Past this many items the delay stops growing, matching the site's cap. */
    const val MaxStaggeredItems = 8

    /** How far a revealing element rises into place. */
    val RevealRise = 20.dp

    /** Press feedback. Slightly underdamped so it settles with a little life. */
    fun <T> pressSpring(): SpringSpec<T> = spring(
        dampingRatio = 0.55f,
        stiffness = Spring.StiffnessMediumLow,
    )

    const val PressedScale = 0.97f
}
