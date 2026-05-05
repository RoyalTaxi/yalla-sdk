package uz.yalla.primitives.pin

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.util.or0
import uz.yalla.resources.Res
import uz.yalla.resources.format_time_min_short
import uz.yalla.resources.img_spinner

/**
 * Inner content of a [LocationPin] — the circular body that can render either
 * a spinner (during `jumping`), a custom icon, or a numeric timeout countdown.
 */
@Composable
internal fun PinContent(
    timeout: Int?,
    jumping: Boolean,
    icon: @Composable () -> Unit,
    timeoutStyle: androidx.compose.ui.text.TextStyle,
    timeoutLabelStyle: androidx.compose.ui.text.TextStyle,
    colors: LocationPinColors,
    dimens: LocationPinDimens,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        label = "rotation",
        animationSpec =
            infiniteRepeatable(
                repeatMode = RepeatMode.Restart,
                animation =
                    tween(
                        easing = FastOutSlowInEasing,
                        durationMillis = JUMP_CYCLE_DURATION_MS * 2
                    )
            )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(dimens.contentSize)
                .aspectRatio(1f)
                .background(
                    shape = dimens.contentShape,
                    color = colors.background
                ).border(
                    width = dimens.borderWidth,
                    shape = dimens.contentShape,
                    brush = colors.border
                )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when {
                jumping -> {
                    Image(
                        painter = painterResource(Res.drawable.img_spinner),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colors.icon, BlendMode.SrcIn),
                        modifier =
                            Modifier
                                .size(18.dp)
                                .graphicsLayer { rotationZ = rotation }
                    )
                }
                timeout == null -> {
                    icon()
                }
                else -> {
                    Text(
                        text = timeout.or0().coerceAtLeast(1).toString(),
                        color = colors.text,
                        style = timeoutStyle
                    )
                    Text(
                        text = stringResource(Res.string.format_time_min_short),
                        color = colors.text,
                        style =
                            timeoutLabelStyle.copy(
                                fontSize = 9.sp,
                                lineHeight = 8.sp
                            )
                    )
                }
            }
        }
    }
}
