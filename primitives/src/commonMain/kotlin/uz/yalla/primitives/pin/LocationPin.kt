package uz.yalla.primitives.pin

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
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import uz.yalla.core.util.or0
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.common_status_loading
import uz.yalla.resources.format_time_min_short
import uz.yalla.resources.icons.FocusOrigin
import uz.yalla.resources.icons.PinShadow
import uz.yalla.resources.icons.YallaIcons
import uz.yalla.resources.img_spinner

/**
 * Color configuration for [LocationPin].
 *
 * Controls the visual appearance of the pin body, stick, header tooltip,
 * and content elements. Use [LocationPinDefaults.colors] to create with
 * theme-aware defaults.
 *
 * @param background Background color of the circular pin body.
 * @param border Gradient brush applied as the pin body border.
 * @param stick Color of the vertical stick connecting pin body to shadow.
 * @param stickBorder Border color around the stick.
 * @param header Background color of the address header tooltip.
 * @param headerText Text color inside the address header tooltip.
 * @param icon Tint color for the default focus-origin icon inside the pin body.
 * @param text Color for timeout text displayed inside the pin body.
 * @since 0.0.1
 */
@Immutable
data class LocationPinColors(
    val background: Color,
    val border: Brush,
    val stick: Color,
    val stickBorder: Color,
    val header: Color,
    val headerText: Color,
    val icon: Color,
    val text: Color,
)

/**
 * Dimension configuration for [LocationPin].
 *
 * Controls the sizes, shapes, and offsets of the pin body, stick, shadow,
 * and header tooltip. Use [LocationPinDefaults.dimens] to create with
 * standard values.
 *
 * @param stickHeight Height of the vertical stick below the pin body.
 * @param stickWidth Width of the vertical stick.
 * @param jumpHeight Maximum vertical displacement during jump animation.
 * @param contentSize Width and height of the circular pin body.
 * @param contentShape Shape of the pin body (typically rounded square).
 * @param borderWidth Width of the gradient border around the pin body.
 * @param shadowSize Size of the shadow ellipse rendered at the pin base.
 * @param headerShape Shape of the address header tooltip above the pin.
 * @param headerVerticalPadding Vertical padding inside the header tooltip.
 * @param headerHorizontalPadding Horizontal padding inside the header tooltip.
 * @param contentBottomOffset Offset from the shadow center to the pin body bottom.
 * @param headerBottomOffset Offset from the pin body top to the header tooltip bottom.
 * @since 0.0.1
 */
@Immutable
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
    val headerBottomOffset: Dp,
)

private const val SHADOW_SHRINK_DURATION_MS = 400
private const val JUMP_CYCLE_DURATION_MS = 700

/**
 * Animated map location pin with address tooltip and timeout display.
 *
 * Renders a styled pin marker composed of a circular body, a vertical stick,
 * and a ground shadow. The pin supports several visual states:
 * - **Idle**: Displays a static icon (default: focus-origin) inside the pin body.
 * - **Jumping**: Bounces vertically with a rotating spinner, indicating map interaction
 *   or location search.
 * - **Timeout**: Shows estimated arrival time (in minutes) inside the pin body.
 * - **Address**: Displays an address tooltip above the pin body.
 *
 * The pin layout uses [ConstraintLayout] to position the shadow, stick, body,
 * and header relative to each other, with the jump animation applied as a
 * graphics layer translation.
 *
 * ## Usage
 *
 * ```kotlin
 * // Basic pin
 * LocationPin()
 *
 * // Pin with address tooltip
 * LocationPin(address = "Tashkent, Amir Temur ko'chasi")
 *
 * // Jumping pin during map drag
 * LocationPin(jumping = isDragging)
 *
 * // Pin showing ETA
 * LocationPin(timeout = 5)
 * ```
 *
 * @param modifier [Modifier] applied to the [ConstraintLayout] root.
 * @param address Address text for the header tooltip. When `null`, no header is shown.
 * @param timeout Estimated time in minutes to display inside the pin body.
 *   When `null` and not jumping, shows the [icon] instead.
 * @param jumping When `true`, animates the pin with a vertical bounce and shows a spinner.
 * @param loading Currently unused; reserved for future loading states.
 * @param icon Custom composable rendered inside the pin body when not jumping and no timeout.
 *   Defaults to a focus-origin icon.
 * @param timeoutStyle [TextStyle] for the timeout number.
 * @param timeoutLabelStyle [TextStyle] for the "min" label below the timeout number.
 * @param headerStyle [TextStyle] for the address header tooltip text.
 * @param colors [LocationPinColors] that define the visual palette.
 *   See [LocationPinDefaults.colors].
 * @param dimens [LocationPinDimens] that define sizes, shapes, and offsets.
 *   See [LocationPinDefaults.dimens].
 *
 * @see SearchPin for the animated search-in-progress indicator
 * @see LocationPinDefaults for default values
 * @since 0.0.1
 */
@Composable
fun LocationPin(
    modifier: Modifier = Modifier,
    address: String? = null,
    timeout: Int? = null,
    jumping: Boolean = false,
    loading: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    timeoutStyle: TextStyle = System.font.body.base.bold,
    timeoutLabelStyle: TextStyle = System.font.body.small.bold,
    headerStyle: TextStyle = System.font.body.caption,
    colors: LocationPinColors = LocationPinDefaults.colors(),
    dimens: LocationPinDimens = LocationPinDefaults.dimens(),
) {
    val density = LocalDensity.current
    val jumpHeightPx = with(density) { dimens.jumpHeight.toPx() }
    val stickHeightPx = with(density) { dimens.stickHeight.toPx() }

    val jumpOffset = remember { Animatable(0f) }
    val stickVisibleHeight = remember { Animatable(stickHeightPx) }
    val shadowScale = remember { Animatable(1f) }

    LaunchedEffect(jumping) {
        if (jumping) {
            shadowScale.animateTo(
                targetValue = 1.5f,
                animationSpec =
                    tween(
                        durationMillis = SHADOW_SHRINK_DURATION_MS,
                        easing = EaseInOut,
                    ),
            )
        } else {
            shadowScale.animateTo(
                targetValue = 1f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
            )
        }
    }

    LaunchedEffect(jumping) {
        if (jumping) {
            jumpOffset.animateTo(
                targetValue = -jumpHeightPx,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                durationMillis = JUMP_CYCLE_DURATION_MS,
                                easing = EaseInOut,
                            ),
                        repeatMode = RepeatMode.Reverse,
                    ),
            )
        } else {
            jumpOffset.animateTo(
                targetValue = 0f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessVeryLow,
                    ),
            )
        }
    }

    val stickClipHeightDp = with(density) { stickVisibleHeight.value.toDp() }

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
                    },
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
                    },
        )

        PinContent(
            timeout = timeout,
            jumping = jumping,
            icon = icon ?: {
                Icon(
                    painter = rememberVectorPainter(YallaIcons.FocusOrigin),
                    contentDescription = null,
                    tint = colors.icon,
                    modifier = Modifier.size(18.dp),
                )
            },
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
                    },
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
                    },
            )
        }
    }
}

@Composable
private fun PinContent(
    timeout: Int?,
    jumping: Boolean,
    icon: @Composable () -> Unit,
    timeoutStyle: TextStyle,
    timeoutLabelStyle: TextStyle,
    colors: LocationPinColors,
    dimens: LocationPinDimens,
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
                        durationMillis = JUMP_CYCLE_DURATION_MS * 2,
                    ),
            ),
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .size(dimens.contentSize)
                .aspectRatio(1f)
                .background(
                    shape = dimens.contentShape,
                    color = colors.background,
                ).border(
                    width = dimens.borderWidth,
                    shape = dimens.contentShape,
                    brush = colors.border,
                ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when {
                jumping -> {
                    Image(
                        painter = painterResource(Res.drawable.img_spinner),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .size(18.dp)
                                .graphicsLayer { rotationZ = rotation },
                    )
                }
                timeout == null -> {
                    icon()
                }
                else -> {
                    Text(
                        text = timeout.or0().coerceAtLeast(1).toString(),
                        color = colors.text,
                        style = timeoutStyle,
                    )
                    Text(
                        text = stringResource(Res.string.format_time_min_short),
                        color = colors.text,
                        style =
                            timeoutLabelStyle.copy(
                                fontSize = 9.sp,
                                lineHeight = 8.sp,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun PinStick(
    clipHeight: Dp,
    colors: LocationPinColors,
    dimens: LocationPinDimens,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier =
            modifier
                .width(dimens.stickWidth)
                .height(dimens.stickHeight),
    ) {
        Box(
            modifier =
                Modifier
                    .width(dimens.stickWidth)
                    .height(clipHeight)
                    .clipToBounds()
                    .background(
                        shape = CircleShape,
                        color = colors.stick,
                    ).border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = colors.stickBorder,
                    ),
        )
    }
}

@Composable
private fun PinHeader(
    address: String,
    headerStyle: TextStyle,
    colors: LocationPinColors,
    dimens: LocationPinDimens,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = dimens.headerShape,
        color = colors.header,
        modifier = modifier,
    ) {
        Text(
            text =
                address.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.common_status_loading),
            color = colors.headerText,
            style = headerStyle,
            maxLines = 1,
            modifier =
                Modifier
                    .animateContentSize(
                        alignment = Alignment.Center,
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                    ).padding(
                        vertical = dimens.headerVerticalPadding,
                        horizontal = dimens.headerHorizontalPadding,
                    ),
        )
    }
}

/**
 * Default configuration values for [LocationPin].
 *
 * Provides theme-aware defaults for [colors] and standard [dimens]
 * that can be individually overridden.
 * @since 0.0.1
 */
object LocationPinDefaults {

    /**
     * Creates theme-aware color configuration for [LocationPin].
     *
     * @param background Background color of the pin body.
     * @param border Gradient brush for the pin body border.
     * @param stick Color of the vertical stick.
     * @param stickBorder Border color around the stick.
     * @param header Background color of the address tooltip.
     * @param headerText Text color of the address tooltip.
     * @param icon Tint color for the default icon.
     * @param text Color for timeout text.
     */
    @Composable
    fun colors(
        background: Color = System.color.background.base,
        border: Brush = System.color.gradient.sunsetNight,
        stick: Color = System.color.background.brand,
        stickBorder: Color = System.color.background.base,
        header: Color = System.color.icon.base,
        headerText: Color = System.color.background.base,
        icon: Color = System.color.icon.base,
        text: Color = System.color.text.base,
    ): LocationPinColors = LocationPinColors(
        background = background,
        border = border,
        stick = stick,
        stickBorder = stickBorder,
        header = header,
        headerText = headerText,
        icon = icon,
        text = text,
    )

    /**
     * Creates dimension configuration for [LocationPin].
     *
     * @param stickHeight Height of the vertical stick.
     * @param stickWidth Width of the vertical stick.
     * @param jumpHeight Maximum vertical displacement during jump animation.
     * @param contentSize Width and height of the pin body.
     * @param contentShape Shape of the pin body.
     * @param borderWidth Width of the gradient border.
     * @param shadowSize Size of the ground shadow ellipse.
     * @param headerShape Shape of the address tooltip.
     * @param headerVerticalPadding Vertical padding inside the tooltip.
     * @param headerHorizontalPadding Horizontal padding inside the tooltip.
     * @param contentBottomOffset Offset from shadow to pin body bottom.
     * @param headerBottomOffset Offset from pin body top to tooltip bottom.
     */
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
        headerBottomOffset: Dp = 2.dp,
    ): LocationPinDimens = LocationPinDimens(
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
        headerBottomOffset = headerBottomOffset,
    )
}
