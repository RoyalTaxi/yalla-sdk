package uz.yalla.primitives.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

/**
 * Color configuration for [IconButton].
 *
 * Resolves container and content colors based on enabled state.
 * Use [IconButtonDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color when enabled.
 * @param contentColor Icon color when enabled.
 * @param disabledContainerColor Background color when disabled.
 * @param disabledContentColor Icon color when disabled.
 */
@Immutable
data class IconButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    /** Resolves container color based on [enabled] state. */
    fun containerColor(enabled: Boolean): Color =
        if (enabled) containerColor else disabledContainerColor

    /** Resolves content color based on [enabled] state. */
    fun contentColor(enabled: Boolean): Color =
        if (enabled) contentColor else disabledContentColor
}

/**
 * Dimension configuration for [IconButton].
 *
 * Use [IconButtonDefaults.dimens] to create with standard values.
 *
 * @param size Button size (width and height).
 * @param iconSize Icon size constraint.
 * @param shape Container shape.
 */
@Immutable
data class IconButtonDimens(
    val size: Dp,
    val iconSize: Dp,
    val shape: Shape,
)

/**
 * Icon-only button for compact actions.
 *
 * Use for actions represented by icons, like navigation, menu, or close buttons.
 * This button renders a single icon inside a shaped surface — no text, no loading state.
 *
 * ## Usage
 * ```kotlin
 * IconButton(onClick = { closeSheet() }) {
 *     Icon(Icons.Default.Close, contentDescription = "Close")
 * }
 * ```
 *
 * ## Filled Variant
 * ```kotlin
 * IconButton(
 *     onClick = { confirm() },
 *     colors = IconButtonDefaults.filledColors(),
 * ) {
 *     Icon(Icons.Default.Check, contentDescription = "Confirm")
 * }
 * ```
 *
 * @param onClick Called when this button is clicked. Not called when disabled.
 * @param modifier [Modifier] applied to the root container.
 * @param enabled Controls the enabled state. When `false`, the button appears visually
 *   disabled and does not respond to user input.
 * @param colors [IconButtonColors] that resolve colors for different states.
 *   See [IconButtonDefaults.colors] and [IconButtonDefaults.filledColors].
 * @param dimens [IconButtonDimens] that define dimensions and shape.
 *   See [IconButtonDefaults.dimens].
 * @param content The icon content.
 *
 * @see IconButtonDefaults
 */
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.colors(),
    dimens: IconButtonDimens = IconButtonDefaults.dimens(),
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(dimens.size),
        enabled = enabled,
        shape = dimens.shape,
        color = colors.containerColor(enabled),
        contentColor = colors.contentColor(enabled),
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

/**
 * Default configuration values for [IconButton].
 *
 * Provides theme-aware [colors] and standard [dimens] that can be individually overridden.
 */
object IconButtonDefaults {
    /** Default button size. */
    val Size = 44.dp

    /** Default icon size. */
    val IconSize = 22.dp

    /** Default button shape. */
    val Shape: Shape = RoundedCornerShape(12.dp)

    /**
     * Creates [IconButtonColors] with theme-aware defaults.
     *
     * @param containerColor Background color when enabled.
     * @param contentColor Icon color when enabled.
     * @param disabledContainerColor Background color when disabled.
     * @param disabledContentColor Icon color when disabled.
     */
    @Composable
    fun colors(
        containerColor: Color = System.color.background.tertiary,
        contentColor: Color = System.color.icon.base,
        disabledContainerColor: Color = System.color.background.tertiary.copy(alpha = 0.5f),
        disabledContentColor: Color = System.color.icon.disabled,
    ): IconButtonColors = IconButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    /**
     * Creates filled/prominent [IconButtonColors] with theme-aware defaults.
     *
     * Use for high-emphasis icon actions.
     *
     * @param containerColor Background color when enabled.
     * @param contentColor Icon color when enabled.
     * @param disabledContainerColor Background color when disabled.
     * @param disabledContentColor Icon color when disabled.
     */
    @Composable
    fun filledColors(
        containerColor: Color = System.color.button.active,
        contentColor: Color = System.color.icon.white,
        disabledContainerColor: Color = System.color.button.disabled,
        disabledContentColor: Color = System.color.icon.white,
    ): IconButtonColors = IconButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    /**
     * Creates [IconButtonDimens] with standard values.
     *
     * @param size Button size (width and height).
     * @param iconSize Icon size constraint.
     * @param shape Container shape.
     */
    fun dimens(
        size: Dp = Size,
        iconSize: Dp = IconSize,
        shape: Shape = IconButtonDefaults.Shape,
    ): IconButtonDimens = IconButtonDimens(
        size = size,
        iconSize = iconSize,
        shape = shape,
    )
}

@Preview
@Composable
private fun IconButtonPreview() {
    YallaTheme {
        IconButton(onClick = {}) {
            Box(Modifier.size(22.dp))
        }
    }
}
