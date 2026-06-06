package uz.yalla.components.composites.pin

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.util.or0
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.Res
import uz.yalla.resources.common_status_loading
import uz.yalla.resources.format_time_min_short
import uz.yalla.resources.icons.FocusOrigin
import uz.yalla.resources.icons.PinShadow
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.img_spinner

private val ShadowWidth = 18.dp
private val ShadowHeight = 4.dp
private val StickWidth = 6.dp
private val StickHeight = 12.dp
private val JumpHeight = 6.dp
private val ContentSize = 40.dp
private val ContentShape = RoundedCornerShape(14.dp)
private val HeaderShape = RoundedCornerShape(16.dp)
private val IconSize = 18.dp

private const val ShadowExpandedScale = 1.5f
private const val ShadowShrinkDurationMs = 400
private const val JumpCycleDurationMs = 700

@Composable
fun LocationPin(
    address: String? = null,
    timeout: Int? = null,
    jumping: Boolean = false,
    icon: @Composable () -> Unit = {
        Icon(
            painter = rememberVectorPainter(YallaIcons.FocusOrigin),
            contentDescription = null,
            tint = System.color.icon.base,
            modifier = Modifier.size(IconSize)
        )
    },
    modifier: Modifier = Modifier
) {
    val jumpHeightPx = with(LocalDensity.current) { JumpHeight.toPx() }
    val jumpOffset = remember { Animatable(0f) }
    val shadowScale = remember { Animatable(1f) }

    LaunchedEffect(jumping) {
        if (jumping) {
            shadowScale.animateTo(
                targetValue = ShadowExpandedScale,
                animationSpec = tween(ShadowShrinkDurationMs, easing = EaseInOut)
            )
        } else {
            shadowScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)
            )
        }
    }

    LaunchedEffect(jumping) {
        if (jumping) {
            jumpOffset.animateTo(
                targetValue = -jumpHeightPx,
                animationSpec = infiniteRepeatable(
                    animation = tween(JumpCycleDurationMs, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            jumpOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessVeryLow)
            )
        }
    }

    ConstraintLayout(modifier = modifier) {
        val (shadow, stick, content, header) = createRefs()

        Image(
            painter = rememberVectorPainter(YallaIcons.PinShadow),
            contentDescription = null,
            modifier = Modifier
                .size(width = ShadowWidth, height = ShadowHeight)
                .graphicsLayer {
                    scaleX = shadowScale.value
                    scaleY = shadowScale.value
                }
                .constrainAs(shadow) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = parent.start, end = parent.end)
                }
        )

        Box(
            modifier = Modifier
                .width(StickWidth)
                .height(StickHeight)
                .background(shape = CircleShape, color = System.color.background.brand)
                .border(width = 1.dp, shape = CircleShape, color = System.color.background.base)
                .graphicsLayer { translationY = jumpOffset.value }
                .constrainAs(stick) {
                    bottom.linkTo(shadow.bottom, margin = ShadowHeight / 2)
                    linkTo(start = shadow.start, end = shadow.end)
                }
        )

        PinContent(
            timeout = timeout,
            jumping = jumping,
            icon = icon,
            modifier = Modifier
                .graphicsLayer { translationY = jumpOffset.value }
                .constrainAs(content) {
                    bottom.linkTo(stick.top)
                    linkTo(start = stick.start, end = stick.end)
                }
        )

        address?.let { addr ->
            PinHeader(
                address = addr,
                modifier = Modifier.constrainAs(header) {
                    bottom.linkTo(content.top, margin = JumpHeight + 2.dp)
                    linkTo(start = content.start, end = content.end)
                }
            )
        }
    }
}

@Composable
private fun PinContent(
    timeout: Int?,
    jumping: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Restart,
            animation = tween(
                durationMillis = JumpCycleDurationMs * 2,
                easing = FastOutSlowInEasing
            )
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(ContentSize)
            .background(shape = ContentShape, color = System.color.background.base)
            .border(
                width = 2.dp,
                shape = ContentShape,
                brush = System.color.gradient.sunsetNight
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when {
                jumping -> Image(
                    painter = painterResource(Res.drawable.img_spinner),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(System.color.icon.base, BlendMode.SrcIn),
                    modifier = Modifier
                        .size(IconSize)
                        .graphicsLayer { rotationZ = rotation }
                )
                timeout == null -> icon()
                else -> {
                    Text(
                        text = timeout.or0().coerceAtLeast(1).toString(),
                        color = System.color.text.base,
                        style = System.font.body.base.bold
                    )
                    Text(
                        text = stringResource(Res.string.format_time_min_short),
                        color = System.color.text.base,
                        style = System.font.body.small.bold.copy(
                            fontSize = 9.sp,
                            lineHeight = 8.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PinHeader(
    address: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = HeaderShape,
        color = System.color.icon.base,
        modifier = modifier
    ) {
        Text(
            text = address.takeIf { it.isNotBlank() } ?: stringResource(Res.string.common_status_loading),
            color = System.color.background.base,
            style = System.font.body.caption,
            maxLines = 1,
            modifier = Modifier
                .animateContentSize(
                    alignment = Alignment.Center,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                .padding(vertical = 6.dp, horizontal = 12.dp)
        )
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    LocationPin(address = "Tashkent, Amir Temur 1")
}
