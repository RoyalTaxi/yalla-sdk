package uz.yalla.primitives.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Animated linear-gradient shimmer for placeholder skeletons.
 *
 * The modifier draws the underlying content first, then composites a moving
 * linear gradient on top using [Modifier.drawWithContent]. The sweep travels
 * along the diagonal of the modified surface, restarting every 1500ms with
 * [LinearEasing] so the rhythm reads as ambient — not as a progress signal.
 *
 * Intentionally opinionated: there is no `Defaults` / `Colors` data class.
 * Override only when the surface tint demands it (e.g. shimmer over a tinted
 * card). For everything else, the theme's [System.color.background.tertiary]
 * gives a brand-correct sweep on both light and dark schemes.
 *
 * ## Usage
 *
 * ```kotlin
 * // Bare modifier — drop on any surface that already has a shape clip:
 * Box(
 *     modifier = Modifier
 *         .size(120.dp, 16.dp)
 *         .clip(RoundedCornerShape(8.dp))
 *         .shimmer(),
 * )
 *
 * // High-level helpers:
 * SkeletonBox(modifier = Modifier.size(48.dp), shape = CircleShape)
 * SkeletonText(modifier = Modifier.fillMaxWidth(0.6f))
 * ```
 *
 * @param enabled When `false`, the modifier is a no-op so the same composable
 * tree can switch between loading and loaded states without recomposition
 * branches in callers.
 * @param brushColors Override the gradient stops. The list is consumed in
 * order — typical shimmers use 3 stops `[low, high, low]`. When `null`, the
 * default uses [System.color.background.tertiary] in a `[35%, 65%, 35%]`
 * alpha ramp tuned for Yalla surfaces.
 */
fun Modifier.shimmer(
    enabled: Boolean = true,
    brushColors: List<Color>? = null
): Modifier =
    composed {
        if (!enabled) return@composed this

        val baseColor = System.color.background.tertiary
        val colors =
            brushColors ?: listOf(
                baseColor.copy(alpha = 0.35f),
                baseColor.copy(alpha = 0.65f),
                baseColor.copy(alpha = 0.35f)
            )

        val transition = rememberInfiniteTransition(label = "shimmer")
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = SHIMMER_SWEEP_MILLIS, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
            label = "shimmer-progress"
        )

        drawWithContent {
            drawContent()
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@drawWithContent

            // Sweep travels from -width to +width along the diagonal so the
            // gradient visibly enters and leaves the surface every cycle.
            val travel = width * 2f
            val offsetX = -width + travel * progress

            val brush =
                Brush.linearGradient(
                    colors = colors,
                    start = Offset(offsetX, 0f),
                    end = Offset(offsetX + width, height)
                )

            // Drawing a rect over the full size; the modifier chain (clip /
            // shape from the caller) takes care of corner rounding.
            drawRect(brush = brush, size = Size(width, height))
        }
    }

/**
 * Shimmering placeholder rectangle.
 *
 * Renders a `tertiary`-tinted box clipped to [shape] with [Modifier.shimmer]
 * on top. Use for avatars, thumbnails, and image placeholders.
 *
 * ## Usage
 *
 * ```kotlin
 * Row {
 *     SkeletonBox(
 *         modifier = Modifier.size(40.dp),
 *         shape = CircleShape,
 *     )
 *     SkeletonText(modifier = Modifier.fillMaxWidth(0.5f))
 * }
 * ```
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(SKELETON_DEFAULT_RADIUS)
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .background(
                    System.color.background.tertiary
                        .copy(alpha = 0.35f)
                ).shimmer()
    )
}

/**
 * Shimmering placeholder line for text content.
 *
 * Defaults to 16.dp tall — the body-base line height. Pair with a width
 * modifier (e.g. `Modifier.fillMaxWidth(0.7f)`) to vary the line shape
 * between rows for a less mechanical feel.
 *
 * ## Usage
 *
 * ```kotlin
 * Column(verticalArrangement = Arrangement.spacedBy(System.space.scale.xs)) {
 *     SkeletonText(modifier = Modifier.fillMaxWidth(0.8f))
 *     SkeletonText(modifier = Modifier.fillMaxWidth(0.5f), height = 12.dp)
 * }
 * ```
 */
@Composable
fun SkeletonText(
    modifier: Modifier = Modifier,
    height: Dp = 16.dp
) {
    SkeletonBox(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height),
        shape = RoundedCornerShape(height / 2)
    )
}

private const val SHIMMER_SWEEP_MILLIS = 1500
private val SKELETON_DEFAULT_RADIUS: Dp = 12.dp
