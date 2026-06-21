package uz.yalla.foundation.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

private const val DEFAULT_STAGGER_MS = 100
private const val DEFAULT_DURATION_MS = 300
private const val DEFAULT_SLIDE_PX = 40f

/**
 * Animates an item into view with a fade + upward slide, staggered by its position in a list.
 *
 * @param visible drives the animation: `true` reveals, `false` hides.
 * @param index the item's position; its delay is `index * staggerMs` so later items reveal later.
 * @param staggerMs per-index delay step in milliseconds.
 * @param durationMs fade/slide duration in milliseconds.
 * @param slidePx vertical slide distance (in pixels) the item travels while hidden.
 */
@Composable
public fun Modifier.staggerReveal(
    visible: Boolean,
    index: Int,
    staggerMs: Int = DEFAULT_STAGGER_MS,
    durationMs: Int = DEFAULT_DURATION_MS,
    slidePx: Float = DEFAULT_SLIDE_PX
): Modifier {
    val delayMs = index * staggerMs
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = durationMs, delayMillis = delayMs),
        label = "stagger_alpha_$index"
    )
    val slideOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 1f,
        animationSpec = tween(durationMillis = durationMs, delayMillis = delayMs),
        label = "stagger_offset_$index"
    )
    return this.graphicsLayer {
        this.alpha = alpha
        translationY = slideOffset * slidePx
    }
}
