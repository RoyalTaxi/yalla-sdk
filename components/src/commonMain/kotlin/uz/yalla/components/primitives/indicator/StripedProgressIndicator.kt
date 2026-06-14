package uz.yalla.components.primitives.indicator

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import kotlin.math.hypot

private val TrackHeight = 16.dp
private val CornerRadius = 40.dp
private val TrackShape = RoundedCornerShape(CornerRadius)
private val StripeWidth = 7.dp
private val StripeGap = 7.dp
private const val StripeAngle = 30f
private const val StripeAlpha = 0.2f
private const val AnimationDurationMs = 250

@Composable
public fun StripedProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val clamped = progress.coerceIn(0f, 1f)
    val periodPx = with(density) { (StripeWidth + StripeGap).toPx() }

    val transition = rememberInfiniteTransition()
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = periodPx,
        animationSpec =
            infiniteRepeatable(
                repeatMode = RepeatMode.Restart,
                animation =
                    tween(
                        durationMillis = AnimationDurationMs,
                        easing = LinearEasing
                    )
            )
    )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .clip(TrackShape)
                .background(System.color.background.secondary)
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(clamped)
                    .clip(TrackShape)
                    .background(System.color.gradient.sunsetNight)
        ) {
            Canvas(
                modifier =
                    Modifier
                        .matchParentSize()
                        .graphicsLayer { translationX = offset }
            ) {
                val gap = StripeGap.toPx()
                val stripeWidth = StripeWidth.toPx()
                val period = stripeWidth + gap
                val diagonal = hypot(size.width, size.height)
                val count = ((size.width + diagonal * 2) / period).toInt() + 4

                rotate(StripeAngle, pivot = center) {
                    repeat(count) { i ->
                        drawRect(
                            color = Color.White.copy(alpha = StripeAlpha),
                            topLeft = Offset(x = i * period - diagonal - period, y = -diagonal),
                            size = Size(width = stripeWidth, height = diagonal * 2)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        StripedProgressIndicator(progress = 0.5f)
    }
