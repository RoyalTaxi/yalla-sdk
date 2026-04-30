package uz.yalla.primitives.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

/**
 * Color configuration for [TextButton].
 *
 * Resolves container and content colors based on enabled state.
 * Use [TextButtonDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color when enabled.
 * @param contentColor Text and icon color when enabled.
 * @param disabledContainerColor Background color when disabled or loading.
 * @param disabledContentColor Text and icon color when disabled or loading.
 */
@Immutable
data class TextButtonColors(
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
 * Dimension configuration for [TextButton].
 *
 * Use [TextButtonDefaults.dimens] to create with standard values.
 *
 * @param minHeight Minimum button height (touch target).
 * @param contentPadding Padding between container and content.
 * @param shape Container shape.
 * @param iconSpacing Space between icon slots and content.
 */
@Immutable
data class TextButtonDimens(
    val minHeight: Dp,
    val contentPadding: PaddingValues,
    val shape: Shape,
    val iconSpacing: Dp,
)

/**
 * Text-only button for low-emphasis actions like "Skip", "Cancel", or "Resend".
 *
 * Use for tertiary actions that should be less prominent than [PrimaryButton] or [SecondaryButton].
 *
 * ## Building Blocks
 * Delegates to [ButtonLayout] for Container -> Provider -> Layout -> Content structure.
 *
 * ## Usage
 * ```kotlin
 * TextButton(onClick = { skip() }) {
 *     Text("Skip")
 * }
 * ```
 *
 * ## With Loading State
 * ```kotlin
 * TextButton(onClick = { resend() }, loading = isResending) {
 *     Text("Resend")
 * }
 * ```
 *
 * ## With Icons
 * ```kotlin
 * TextButton(
 *     onClick = { learnMore() },
 *     trailingIcon = { Icon(YallaIcons.ArrowRight, contentDescription = null) },
 * ) {
 *     Text("Learn More")
 * }
 * ```
 *
 * @param onClick Called when this button is clicked. Not called when disabled or loading.
 * @param modifier [Modifier] applied to the root container.
 * @param enabled Controls the enabled state. When `false`, the button appears visually
 *   disabled and does not respond to user input.
 * @param loading When `true`, shows a platform-native loading indicator instead of content.
 *   The button is not clickable while loading.
 * @param colors [TextButtonColors] that resolve colors for different states.
 *   See [TextButtonDefaults.colors].
 * @param dimens [TextButtonDimens] that define dimensions and shape.
 *   See [TextButtonDefaults.dimens].
 * @param leadingIcon Optional composable displayed before the content.
 * @param trailingIcon Optional composable displayed after the content.
 * @param content The button content, typically a [Text].
 *
 * @see PrimaryButton
 * @see SecondaryButton
 * @see TextButtonDefaults
 */
@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: TextButtonColors = TextButtonDefaults.colors(),
    dimens: TextButtonDimens = TextButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val isInteractive = enabled && !loading
    ButtonLayout(
        onClick = onClick,
        modifier = modifier,
        enabled = isInteractive,
        loading = loading,
        shape = dimens.shape,
        containerColor = colors.containerColor(isInteractive),
        contentColor = colors.contentColor(isInteractive),
        contentPadding = dimens.contentPadding,
        minHeight = dimens.minHeight,
        iconSpacing = dimens.iconSpacing,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        content = content,
    )
}

/**
 * Default configuration values for [TextButton].
 *
 * Provides theme-aware [colors] and standard [dimens] that can be individually overridden.
 */
object TextButtonDefaults {
    /** Default minimum button height. */
    val MinHeight = 40.dp

    /** Default content padding. */
    val ContentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

    /** Default button shape. */
    val Shape: Shape = RoundedCornerShape(20.dp)

    /**
     * Creates [TextButtonColors] with theme-aware defaults.
     *
     * @param containerColor Background color when enabled.
     * @param contentColor Text/icon color when enabled.
     * @param disabledContainerColor Background color when disabled.
     * @param disabledContentColor Text/icon color when disabled.
     */
    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        contentColor: Color = System.color.text.base,
        disabledContainerColor: Color = Color.Transparent,
        disabledContentColor: Color = System.color.text.subtle,
    ): TextButtonColors = TextButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    /**
     * Creates [TextButtonDimens] with standard values.
     *
     * @param minHeight Minimum button height.
     * @param contentPadding Padding between container and content.
     * @param shape Container shape.
     * @param iconSpacing Space between icons and content.
     */
    fun dimens(
        minHeight: Dp = MinHeight,
        contentPadding: PaddingValues = ContentPadding,
        shape: Shape = Shape,
        iconSpacing: Dp = 8.dp,
    ): TextButtonDimens = TextButtonDimens(
        minHeight = minHeight,
        contentPadding = contentPadding,
        shape = shape,
        iconSpacing = iconSpacing,
    )
}

@Preview
@Composable
private fun TextButtonPreview() {
    YallaTheme {
        Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
            TextButton(onClick = {}) {
                Text("Skip")
            }
        }
    }
}

@Preview
@Composable
private fun TextButtonLoadingPreview() {
    YallaTheme {
        Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
            TextButton(onClick = {}, loading = true) {
                Text("Loading")
            }
        }
    }
}
