package uz.yalla.components.primitive.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.components.model.common.ButtonSize
import uz.yalla.design.theme.System

/**
 * State for [IconButton] component.
 *
 * @property enabled When false, button is disabled.
 * @property size Button size variant.
 */
data class IconButtonState(
    val enabled: Boolean = true,
    val size: ButtonSize = ButtonSize.Medium
)

/**
 * Icon-only button for compact actions.
 *
 * Use for actions represented by icons, like navigation, menu, or close buttons.
 *
 * ## Usage
 *
 * ```kotlin
 * IconButton(
 *     state = IconButtonState(),
 *     onClick = onClose,
 * ) {
 *     Icon(Icons.Default.Close, contentDescription = "Close")
 * }
 * ```
 *
 * @param state Button state containing enabled and size.
 * @param onClick Invoked on click.
 * @param modifier Applied to button.
 * @param colors Color configuration, defaults to [IconButtonDefaults.colors].
 * @param dimens Dimension configuration, defaults to [IconButtonDefaults.dimens].
 * @param content Icon content.
 *
 * @see IconButtonDefaults for default values
 */
@Composable
fun IconButton(
    state: IconButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: IconButtonDefaults.IconButtonColors = IconButtonDefaults.colors(),
    dimens: IconButtonDefaults.IconButtonDimens = IconButtonDefaults.dimens(),
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(dimens.size(state.size)),
        enabled = state.enabled,
        shape = dimens.shape,
        color = colors.containerColor(state.enabled),
        contentColor = colors.contentColor(state.enabled),
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

/**
 * Default configuration values for [IconButton].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object IconButtonDefaults {
    /**
     * Color configuration for [IconButton].
     *
     * @param container Background color when enabled.
     * @param content Icon color when enabled.
     * @param disabledContainer Background color when disabled.
     * @param disabledContent Icon color when disabled.
     */
    data class IconButtonColors(
        val container: Color,
        val content: Color,
        val disabledContainer: Color,
        val disabledContent: Color,
    ) {
        fun containerColor(enabled: Boolean): Color = if (enabled) container else disabledContainer

        fun contentColor(enabled: Boolean): Color = if (enabled) content else disabledContent
    }

    @Composable
    fun colors(
        container: Color = System.color.backgroundTertiary,
        content: Color = System.color.iconBase,
        disabledContainer: Color = System.color.backgroundTertiary.copy(alpha = 0.5f),
        disabledContent: Color = System.color.iconDisabled,
    ) = IconButtonColors(
        container = container,
        content = content,
        disabledContainer = disabledContainer,
        disabledContent = disabledContent,
    )

    @Composable
    fun filledColors(
        container: Color = System.color.buttonActive,
        content: Color = System.color.iconWhite,
        disabledContainer: Color = System.color.buttonDisabled,
        disabledContent: Color = System.color.iconWhite,
    ) = IconButtonColors(
        container = container,
        content = content,
        disabledContainer = disabledContainer,
        disabledContent = disabledContent,
    )

    /**
     * Dimension configuration for [IconButton].
     *
     * @param smallSize Button size for small variant.
     * @param mediumSize Button size for medium variant.
     * @param largeSize Button size for large variant.
     * @param smallIconSize Icon size for small variant.
     * @param mediumIconSize Icon size for medium variant.
     * @param largeIconSize Icon size for large variant.
     * @param cornerRadius Corner radius of the button shape.
     * @param shape Button shape.
     */
    data class IconButtonDimens(
        val smallSize: Dp,
        val mediumSize: Dp,
        val largeSize: Dp,
        val smallIconSize: Dp,
        val mediumIconSize: Dp,
        val largeIconSize: Dp,
        val cornerRadius: Dp,
        val shape: Shape,
    ) {
        fun size(size: ButtonSize): Dp =
            when (size) {
                ButtonSize.Small -> smallSize
                ButtonSize.Medium -> mediumSize
                ButtonSize.Large -> largeSize
            }

        fun iconSize(size: ButtonSize): Dp =
            when (size) {
                ButtonSize.Small -> smallIconSize
                ButtonSize.Medium -> mediumIconSize
                ButtonSize.Large -> largeIconSize
            }
    }

    @Composable
    fun dimens(
        smallSize: Dp = 36.dp,
        mediumSize: Dp = 44.dp,
        largeSize: Dp = 52.dp,
        smallIconSize: Dp = 18.dp,
        mediumIconSize: Dp = 22.dp,
        largeIconSize: Dp = 26.dp,
        cornerRadius: Dp = 12.dp,
        shape: Shape = RoundedCornerShape(cornerRadius),
    ) = IconButtonDimens(
        smallSize = smallSize,
        mediumSize = mediumSize,
        largeSize = largeSize,
        smallIconSize = smallIconSize,
        mediumIconSize = mediumIconSize,
        largeIconSize = largeIconSize,
        cornerRadius = cornerRadius,
        shape = shape,
    )
}

@Preview
@Composable
private fun IconButtonPreview() {
    IconButton(
        state = IconButtonState(),
        onClick = {}
    ) {
        Box(Modifier.size(22.dp))
    }
}
