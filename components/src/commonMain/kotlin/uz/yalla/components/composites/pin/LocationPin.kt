package uz.yalla.components.composites.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.FocusOrigin
import uz.yalla.resources.icons.PinShadow
import uz.yalla.resources.icons.YallaIcons

private val IconSize = 18.dp

private const val SHADOW_EXPANDED_SCALE = 1.5f
private const val SHADOW_SHRINK_DURATION_MS = 400
internal const val JUMP_CYCLE_DURATION_MS = 700

@Immutable
public data class LocationPinColors(
    val background: Color,
    val border: Brush,
    val stick: Color,
    val stickBorder: Color,
    val header: Color,
    val headerText: Color,
    val icon: Color,
    val text: Color
)

@Immutable
public data class LocationPinDimens(
    val stickHeight: Dp,
    val stickWidth: Dp,
    val jumpHeight: Dp,
    val contentSize: Dp,
    val contentShape: Shape,
    val borderWidth: Dp,
    val shadowSize: Dp,
    val headerShape: Shape,
    val headerVerticalPadding: Dp,
    val headerHorizontalPadding: Dp,
    val contentBottomOffset: Dp,
    val headerBottomOffset: Dp
)

public object LocationPinDefaults {
    @Composable
    public fun colors(
        background: Color = System.color.background.base,
        border: Brush = System.color.gradient.sunsetNight,
        stick: Color = System.color.background.brand,
        stickBorder: Color = System.color.background.base,
        header: Color = System.color.icon.base,
        headerText: Color = System.color.background.base,
        icon: Color = System.color.icon.base,
        text: Color = System.color.text.base
    ): LocationPinColors =
        LocationPinColors(
            background = background,
            border = border,
            stick = stick,
            stickBorder = stickBorder,
            header = header,
            headerText = headerText,
            icon = icon,
            text = text
        )

    @Composable
    public fun dimens(
        stickHeight: Dp = 12.dp,
        stickWidth: Dp = 6.dp,
        jumpHeight: Dp = 6.dp,
        contentSize: Dp = 40.dp,
        contentShape: Shape = RoundedCornerShape(14.dp),
        borderWidth: Dp = 2.dp,
        shadowSize: Dp = 12.dp,
        headerShape: Shape = RoundedCornerShape(16.dp),
        headerVerticalPadding: Dp = 6.dp,
        headerHorizontalPadding: Dp = 12.dp,
        contentBottomOffset: Dp = 6.dp,
        headerBottomOffset: Dp = 2.dp
    ): LocationPinDimens =
        LocationPinDimens(
            stickHeight = stickHeight,
            stickWidth = stickWidth,
            jumpHeight = jumpHeight,
            contentSize = contentSize,
            contentShape = contentShape,
            borderWidth = borderWidth,
            shadowSize = shadowSize,
            headerShape = headerShape,
            headerVerticalPadding = headerVerticalPadding,
            headerHorizontalPadding = headerHorizontalPadding,
            contentBottomOffset = contentBottomOffset,
            headerBottomOffset = headerBottomOffset
        )
}

@Composable
public fun LocationPin(
    modifier: Modifier = Modifier,
    address: String? = null,
    timeout: Int? = null,
    jumping: Boolean = false,
    icon: (@Composable () -> Unit)? = null,
    timeoutStyle: TextStyle = System.font.body.base.bold,
    timeoutLabelStyle: TextStyle = System.font.body.small.bold,
    headerStyle: TextStyle = System.font.body.caption,
    colors: LocationPinColors = LocationPinDefaults.colors(),
    dimens: LocationPinDimens = LocationPinDefaults.dimens()
) {
    val density = LocalDensity.current
    val jumpHeightPx = with(density) { dimens.jumpHeight.toPx() }
    val stickHeightPx = with(density) { dimens.stickHeight.toPx() }
    val jumpOffset = remember { Animatable(0f) }
    val stickVisibleHeight = remember { Animatable(stickHeightPx) }
    val shadowScale = remember { Animatable(1f) }
    val stickClipHeightDp = with(density) { stickVisibleHeight.value.toDp() }
    val resolvedIcon: @Composable () -> Unit =
        icon ?: {
            Icon(
                painter = rememberVectorPainter(YallaIcons.FocusOrigin),
                contentDescription = null,
                tint = colors.icon,
                modifier = Modifier.size(IconSize)
            )
        }

    LaunchedEffect(jumping) {
        if (jumping) {
            shadowScale.animateTo(
                targetValue = SHADOW_EXPANDED_SCALE,
                animationSpec = tween(SHADOW_SHRINK_DURATION_MS, easing = EaseInOut)
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
                animationSpec =
                    infiniteRepeatable(
                        animation = tween(JUMP_CYCLE_DURATION_MS, easing = EaseInOut),
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
            modifier =
                Modifier
                    .size(dimens.shadowSize)
                    .graphicsLayer {
                        scaleX = shadowScale.value
                        scaleY = shadowScale.value
                    }.constrainAs(shadow) {
                        linkTo(top = parent.top, bottom = parent.bottom)
                        linkTo(start = parent.start, end = parent.end)
                    }
        )

        PinStick(
            clipHeight = stickClipHeightDp,
            colors = colors,
            dimens = dimens,
            modifier =
                Modifier
                    .graphicsLayer { translationY = jumpOffset.value }
                    .constrainAs(stick) {
                        top.linkTo(content.bottom)
                        linkTo(start = parent.start, end = parent.end)
                    }
        )

        PinContent(
            timeout = timeout,
            jumping = jumping,
            icon = resolvedIcon,
            timeoutStyle = timeoutStyle,
            timeoutLabelStyle = timeoutLabelStyle,
            colors = colors,
            dimens = dimens,
            modifier =
                Modifier
                    .graphicsLayer { translationY = jumpOffset.value }
                    .constrainAs(content) {
                        bottom.linkTo(shadow.bottom, margin = dimens.stickHeight + dimens.contentBottomOffset)
                        linkTo(start = parent.start, end = parent.end)
                    }
        )

        address?.let { addr ->
            PinHeader(
                address = addr,
                headerStyle = headerStyle,
                colors = colors,
                dimens = dimens,
                modifier =
                    Modifier.constrainAs(header) {
                        bottom.linkTo(content.top, margin = dimens.jumpHeight + dimens.headerBottomOffset)
                        linkTo(start = parent.start, end = parent.end)
                    }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        LocationPin(address = "Tashkent, Amir Temur 1")
    }
