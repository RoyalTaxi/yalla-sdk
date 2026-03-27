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
 * Draw-phase-only stagger reveal modifier.
 *
 * Content is always composed and measured (takes space in layout),
 * but visually hidden until [visible] becomes true. Each [index]
 * adds [staggerMs] delay. Uses [graphicsLayer] so animation
 * triggers zero recomposition and zero relayout.
 *
 * @param visible whether the content should be revealed
 * @param index stagger position — higher index = later reveal
 * @param staggerMs delay between consecutive items
 * @param durationMs animation duration per item
 * @param slidePx vertical slide distance in pixels
 */
@Composable
fun Modifier.staggerReveal(
    visible: Boolean,
    index: Int,
    staggerMs: Int = DEFAULT_STAGGER_MS,
    durationMs: Int = DEFAULT_DURATION_MS,
    slidePx: Float = DEFAULT_SLIDE_PX,
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
