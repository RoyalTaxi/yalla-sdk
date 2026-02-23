package uz.yalla.components.primitive.pin

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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.components.util.or0
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.common_status_loading
import uz.yalla.resources.format_time_min_short
import uz.yalla.resources.ic_focus_origin
import uz.yalla.resources.ic_pin_loading
import uz.yalla.resources.ic_pin_shadow

/**
 * Animated location pin with address label and timeout display.
 *
 * The pin supports multiple states:
 * - **Idle**: Shows focus icon or timeout countdown
 * - **Jumping**: Animated bouncing with loading spinner
 * - **With address**: Shows address label above the pin
 *
 * ## Usage
 *
 * ```kotlin
 * // Simple idle pin
 * LocationPin(
 *     state = LocationPinState()
 * )
 *
 * // Pin with address and timeout
 * LocationPin(
 *     state = LocationPinState(
 *         address = "Home",
 *         timeout = 5
 *     )
 * )
 *
 * // Searching/jumping state
 * LocationPin(
 *     state = LocationPinState(
 *         jumping = true,
 *         address = "Searching..."
 *     )
 * )
 * ```
 *
 * @param state Pin state containing address, timeout, and animation flags.
 * @param modifier Modifier for the root layout.
 * @param colors Color configuration, defaults to [LocationPinDefaults.colors].
 * @param dimens Dimension configuration, defaults to [LocationPinDefaults.dimens].
 * @param animation Animation configuration, defaults to [LocationPinDefaults.animation].
 *
 * @see LocationPinDefaults for default values
 */
@Composable
fun LocationPin(
    state: LocationPinState,
    modifier: Modifier = Modifier,
    colors: LocationPinDefaults.LocationPinColors = LocationPinDefaults.colors(),
    dimens: LocationPinDefaults.LocationPinDimens = LocationPinDefaults.dimens(),
    animation: LocationPinDefaults.LocationPinAnimation = LocationPinDefaults.animation(),
) {
    val density = LocalDensity.current
    val jumpHeightPx = with(density) { dimens.jumpHeight.toPx() }
    val stickHeightPx = with(density) { dimens.stickHeight.toPx() }

    val jumpOffset = remember { Animatable(0f) }
    val stickVisibleHeight = remember { Animatable(stickHeightPx) }
    val shadowScale = remember { Animatable(1f) }

    LaunchedEffect(state.jumping) {
        if (state.jumping) {
            shadowScale.animateTo(
                targetValue = 1.5f,
                animationSpec =
                    tween(
                        durationMillis = animation.shadowShrinkDurationMs,
                        easing = EaseInOut
                    )
            )
        } else {
            shadowScale.animateTo(
                targetValue = 1f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
            )
        }
    }

    LaunchedEffect(state.jumping) {
        if (state.jumping) {
            jumpOffset.animateTo(
                targetValue = -jumpHeightPx,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                durationMillis = animation.jumpCycleDurationMs,
                                easing = EaseInOut
                            ),
                        repeatMode = RepeatMode.Reverse
                    )
            )
        } else {
            jumpOffset.animateTo(
                targetValue = 0f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
            )
        }
    }

    val stickClipHeightDp = with(density) { stickVisibleHeight.value.toDp() }

    ConstraintLayout(modifier = modifier) {
        val (shadow, stick, content, header) = createRefs()

        Image(
            painter = painterResource(Res.drawable.ic_pin_shadow),
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
            timeout = state.timeout,
            jumping = state.jumping,
            colors = colors,
            dimens = dimens,
            animation = animation,
            modifier =
                Modifier
                    .graphicsLayer { translationY = jumpOffset.value }
                    .constrainAs(content) {
                        bottom.linkTo(shadow.bottom, margin = dimens.stickHeight + dimens.contentBottomOffset)
                        linkTo(start = parent.start, end = parent.end)
                    }
        )

        state.address?.let { address ->
            PinHeader(
                address = address,
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

@Composable
private fun PinContent(
    timeout: Int?,
    jumping: Boolean,
    colors: LocationPinDefaults.LocationPinColors,
    dimens: LocationPinDefaults.LocationPinDimens,
    animation: LocationPinDefaults.LocationPinAnimation,
    modifier: Modifier = Modifier,
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
                        durationMillis = animation.jumpCycleDurationMs * 2
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
                        painter = painterResource(Res.drawable.ic_pin_loading),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(18.dp)
                                .graphicsLayer { rotationZ = rotation }
                    )
                }
                timeout == null -> {
                    Icon(
                        painter = painterResource(Res.drawable.ic_focus_origin),
                        contentDescription = null,
                        tint = colors.icon
                    )
                }
                else -> {
                    Text(
                        text = timeout.or0().coerceAtLeast(1).toString(),
                        color = colors.text,
                        style = uz.yalla.design.theme.System.font.body.base.bold
                    )
                    Text(
                        text = stringResource(Res.string.format_time_min_short),
                        color = colors.text,
                        style =
                            uz.yalla.design.theme.System.font.body.small.bold.copy(
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
private fun PinStick(
    clipHeight: Dp,
    colors: LocationPinDefaults.LocationPinColors,
    dimens: LocationPinDefaults.LocationPinDimens,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier =
            modifier
                .width(dimens.stickWidth)
                .height(dimens.stickHeight)
    ) {
        Box(
            modifier =
                Modifier
                    .width(dimens.stickWidth)
                    .height(clipHeight)
                    .clipToBounds()
                    .background(
                        shape = CircleShape,
                        color = colors.stick
                    ).border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = colors.stickBorder
                    )
        )
    }
}

@Composable
private fun PinHeader(
    address: String,
    colors: LocationPinDefaults.LocationPinColors,
    dimens: LocationPinDefaults.LocationPinDimens,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = dimens.headerShape,
        color = colors.header,
        modifier = modifier
    ) {
        Text(
            text =
                address.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.common_status_loading),
            color = colors.headerText,
            style = uz.yalla.design.theme.System.font.body.caption,
            maxLines = 1,
            modifier =
                Modifier
                    .animateContentSize(
                        alignment = Alignment.Center,
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                    ).padding(
                        vertical = dimens.headerVerticalPadding,
                        horizontal = dimens.headerHorizontalPadding
                    )
        )
    }
}

/**
 * Default configuration values for [LocationPin].
 *
 * Provides theme-aware defaults for [colors], [style], [dimens], and [animation] that can be overridden.
 */
object LocationPinDefaults {
    /**
     * Color configuration for [LocationPin].
     *
     * @param background Background color of the pin content.
     * @param border Border brush (gradient) of the pin content.
     * @param stick Color of the stick.
     * @param stickBorder Border color of the stick.
     * @param header Background color of the address header.
     * @param headerText Text color of the address header.
     * @param icon Icon tint color.
     * @param text Text color for timeout display.
     */
    data class LocationPinColors(
        val background: Color,
        val border: Brush,
        val stick: Color,
        val stickBorder: Color,
        val header: Color,
        val headerText: Color,
        val icon: Color,
        val text: Color
    )

    @Composable
    fun colors(
        background: Color = System.color.backgroundBase,
        border: Brush = System.color.sunsetNight,
        stick: Color = System.color.backgroundBrandBase,
        stickBorder: Color = System.color.backgroundBase,
        header: Color = System.color.iconBase,
        headerText: Color = System.color.backgroundBase,
        icon: Color = System.color.iconBase,
        text: Color = System.color.textBase
    ) = LocationPinColors(
        background = background,
        border = border,
        stick = stick,
        stickBorder = stickBorder,
        header = header,
        headerText = headerText,
        icon = icon,
        text = text
    )

    /**
     * Text style configuration for [LocationPin].
     *
     * @param timeout Style for timeout number text.
     * @param timeoutLabel Style for timeout label text.
     * @param header Style for header text.
     */
    data class LocationPinStyle(
        val timeout: TextStyle,
        val timeoutLabel: TextStyle,
        val header: TextStyle
    )

    @Composable
    fun style(
        timeout: TextStyle = System.font.body.base.bold,
        timeoutLabel: TextStyle = System.font.body.small.bold,
        header: TextStyle = System.font.body.caption
    ) = LocationPinStyle(
        timeout = timeout,
        timeoutLabel = timeoutLabel,
        header = header
    )

    /**
     * Dimension configuration for [LocationPin].
     *
     * @param stickHeight Height of the stick connecting pin to shadow.
     * @param stickWidth Width of the stick.
     * @param jumpHeight Maximum jump height during animation.
     * @param contentSize Size of the main pin content box.
     * @param contentShape Shape of the content box.
     * @param borderWidth Border width of the content box.
     * @param shadowSize Size of the shadow ellipse.
     * @param headerShape Shape of the header label.
     * @param headerVerticalPadding Vertical padding of the header label.
     * @param headerHorizontalPadding Horizontal padding of the header label.
     * @param contentBottomOffset Extra offset between pin content bottom and shadow.
     * @param headerBottomOffset Extra offset between header bottom and pin content top.
     */
    data class LocationPinDimens(
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

    @Composable
    fun dimens(
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
    ) = LocationPinDimens(
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

    /**
     * Animation configuration for [LocationPin].
     *
     * @param shadowShrinkDurationMs Duration for shadow shrink animation in milliseconds.
     * @param jumpCycleDurationMs Duration for one jump cycle in milliseconds.
     */
    data class LocationPinAnimation(
        val shadowShrinkDurationMs: Int,
        val jumpCycleDurationMs: Int
    )

    fun animation(
        shadowShrinkDurationMs: Int = 400,
        jumpCycleDurationMs: Int = 700
    ) = LocationPinAnimation(
        shadowShrinkDurationMs = shadowShrinkDurationMs,
        jumpCycleDurationMs = jumpCycleDurationMs
    )
}
