package uz.yalla.primitives.indicator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import kotlin.math.hypot

/**
 * Animated striped progress bar with gradient fill.
 *
 * Displays a horizontal progress bar with animated diagonal stripes
 * that move from left to right.
 *
 * ## Usage
 *
 * ```kotlin
 * // Basic usage
 * StripedProgressbar(progress = 0.5f)
 *
 * // Custom colors
 * StripedProgressbar(
 *     progress = downloadProgress,
 *     colors = StripedProgressbarDefaults.colors(
 *         track = Color.Gray,
 *         indicator = Brush.horizontalGradient(listOf(Color.Blue, Color.Cyan))
 *     )
 * )
 * ```
 *
 * @param progress Progress value between 0.0 and 1.0.
 * @param modifier Modifier for the progress bar.
 * @param colors Color configuration, defaults to [StripedProgressbarDefaults.colors].
 * @param dimens Dimension configuration, defaults to [StripedProgressbarDefaults.dimens].
 *
 * @see StripedProgressbarDefaults for default values
 */
@Composable
fun StripedProgressbar(
    progress: Float,
    modifier: Modifier = Modifier,
    colors: StripedProgressbarDefaults.StripedProgressbarColors = StripedProgressbarDefaults.colors(),
    dimens: StripedProgressbarDefaults.StripedProgressbarDimens = StripedProgressbarDefaults.dimens(),
) {
    val clamped = progress.coerceIn(0f, 1f)
    val density = LocalDensity.current
    val periodPx =
        with(density) {
            (dimens.stripeWidth + dimens.stripeGap).toPx()
        }

    val transition = rememberInfiniteTransition(label = "stripe")
    val animatedOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = periodPx,
        label = "offset",
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = dimens.animationDurationMs,
                        easing = LinearEasing
                    ),
                repeatMode = RepeatMode.Restart
            )
    )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(dimens.height)
                .clip(dimens.shape)
                .background(colors.track)
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(clamped)
                    .clip(dimens.shape)
                    .background(colors.indicator)
        ) {
            Canvas(
                modifier =
                    Modifier
                        .matchParentSize()
                        .graphicsLayer { translationX = animatedOffset }
            ) {
                val stripeWidthPx = dimens.stripeWidth.toPx()
                val gapPx = dimens.stripeGap.toPx()
                val period = stripeWidthPx + gapPx
                val diagonal = hypot(size.width, size.height)
                val stripeCount = ((size.width + diagonal * 2) / period).toInt() + 4

                rotate(dimens.stripeAngle, pivot = center) {
                    repeat(stripeCount) { i ->
                        drawRect(
                            color = colors.stripe,
                            topLeft = Offset(x = i * period - diagonal - period, y = -diagonal),
                            size = Size(width = stripeWidthPx, height = diagonal * 2)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Default values for [StripedProgressbar].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object StripedProgressbarDefaults {
    /**
     * Color configuration for [StripedProgressbar].
     *
     * @param track Background track color.
     * @param indicator Progress indicator brush (gradient).
     * @param stripe Stripe overlay color (semi-transparent).
     */
    data class StripedProgressbarColors(
        val track: Color,
        val indicator: Brush,
        val stripe: Color,
    )

    @Composable
    fun colors(
        track: Color = System.color.backgroundSecondary,
        indicator: Brush = System.color.sunsetNight,
        stripe: Color = Color.White.copy(alpha = 0.2f),
    ): StripedProgressbarColors =
        StripedProgressbarColors(
            track = track,
            indicator = indicator,
            stripe = stripe,
        )

    /**
     * Dimension configuration for [StripedProgressbar].
     *
     * @param shape Progress bar shape.
     * @param height Progress bar height.
     * @param stripeWidth Width of each stripe.
     * @param stripeGap Gap between stripes.
     * @param stripeAngle Angle of stripes in degrees.
     * @param animationDurationMs Animation duration for one stripe cycle.
     */
    data class StripedProgressbarDimens(
        val shape: Shape,
        val height: Dp,
        val stripeWidth: Dp,
        val stripeGap: Dp,
        val stripeAngle: Float,
        val animationDurationMs: Int,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(40.dp),
        height: Dp = 16.dp,
        stripeWidth: Dp = 7.dp,
        stripeGap: Dp = 7.dp,
        stripeAngle: Float = 30f,
        animationDurationMs: Int = 250,
    ): StripedProgressbarDimens =
        StripedProgressbarDimens(
            shape = shape,
            height = height,
            stripeWidth = stripeWidth,
            stripeGap = stripeGap,
            stripeAngle = stripeAngle,
            animationDurationMs = animationDurationMs,
        )
}
