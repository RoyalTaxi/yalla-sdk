package uz.yalla.primitives.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.FocusOrigin
import uz.yalla.resources.icons.PinShadow
import uz.yalla.resources.icons.YallaIcons

/**
 * Color configuration for [LocationPin].
 *
 * Controls the visual appearance of the pin body, stick, header tooltip,
 * and content elements. Use [LocationPinDefaults.colors] to create with
 * theme-aware defaults.
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
    val text: Color
)

/**
 * Dimension configuration for [LocationPin].
 *
 * Controls the sizes, shapes, and offsets of the pin body, stick, shadow,
 * and header tooltip. Use [LocationPinDefaults.dimens] to create with
 * standard values.
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
    val headerBottomOffset: Dp
)

internal const val SHADOW_SHRINK_DURATION_MS = 400
internal const val JUMP_CYCLE_DURATION_MS = 700

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
 */
@Composable
fun LocationPin(
    modifier: Modifier = Modifier,
    address: String? = null,
    timeout: Int? = null,
    jumping: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
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

    LaunchedEffect(jumping) {
        if (jumping) {
            shadowScale.animateTo(
                targetValue = 1.5f,
                animationSpec =
                    tween(
                        durationMillis = SHADOW_SHRINK_DURATION_MS,
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

    LaunchedEffect(jumping) {
        if (jumping) {
            jumpOffset.animateTo(
                targetValue = -jumpHeightPx,
                animationSpec =
                    infiniteRepeatable(
                        animation =
                            tween(
                                durationMillis = JUMP_CYCLE_DURATION_MS,
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
            icon =
                icon ?: {
                    Icon(
                        painter = rememberVectorPainter(YallaIcons.FocusOrigin),
                        contentDescription = null,
                        tint = colors.icon,
                        modifier = Modifier.size(18.dp)
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

/**
 * Default configuration values for [LocationPin].
 *
 * Provides theme-aware defaults for [colors] and standard [dimens]
 * that can be individually overridden.
 */
object LocationPinDefaults {
    /** Creates theme-aware color configuration for [LocationPin]. */
    @Composable
    fun colors(
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

    /** Creates dimension configuration for [LocationPin]. */
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
