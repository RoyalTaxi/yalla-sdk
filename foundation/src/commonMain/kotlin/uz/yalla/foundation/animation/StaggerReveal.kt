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
 * ## Usage
 *
 * ```kotlin
 * Column {
 *     items.forEachIndexed { index, item ->
 *         Text(
 *             text = item.title,
 *             modifier = Modifier.staggerReveal(
 *                 visible = isRevealed,
 *                 index = index,
 *             ),
 *         )
 *     }
 * }
 * ```
 *
 * @param visible Whether the content should be revealed.
 * @param index Stagger position — higher index = later reveal.
 * @param staggerMs Delay in milliseconds between consecutive items.
 * @param durationMs Animation duration in milliseconds per item.
 * @param slidePx Vertical slide distance in pixels.
 * @return [Modifier] with stagger-reveal animation applied via [graphicsLayer].
 * @since 0.0.1
 * @see graphicsLayer
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
