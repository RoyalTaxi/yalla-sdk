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

/**
 * Color configuration for [SecondaryButton].
 *
 * Resolves container and content colors based on enabled state.
 * Use [SecondaryButtonDefaults.colors] to create with theme-aware defaults.
 *
 * @param containerColor Background color when enabled.
 * @param contentColor Text and icon color when enabled.
 * @param disabledContainerColor Background color when disabled or loading.
 * @param disabledContentColor Text and icon color when disabled or loading.
 */
@Immutable
data class SecondaryButtonColors(
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
 * Dimension configuration for [SecondaryButton].
 *
 * Use [SecondaryButtonDefaults.dimens] to create with standard values.
 *
 * @param minHeight Minimum button height (touch target).
 * @param contentPadding Padding between container and content.
 * @param shape Container shape.
 * @param iconSpacing Space between icon slots and content.
 */
@Immutable
data class SecondaryButtonDimens(
    val minHeight: Dp,
    val contentPadding: PaddingValues,
    val shape: Shape,
    val iconSpacing: Dp,
)

/**
 * Secondary action button for alternative or less prominent actions.
 *
 * Use for secondary actions on a screen: "Cancel", "Skip", "Later".
 * For the main action, see [PrimaryButton]. For low-emphasis, see [TextButton].
 *
 * ## Building Blocks
 * Delegates to [ButtonLayout] for Container -> Provider -> Layout -> Content structure.
 *
 * ## Usage
 * ```kotlin
 * SecondaryButton(onClick = { cancel() }) {
 *     Text("Cancel")
 * }
 * ```
 *
 * ## With Loading State
 * ```kotlin
 * SecondaryButton(onClick = { skip() }, loading = isSkipping) {
 *     Text("Skip")
 * }
 * ```
 *
 * ## With Icons
 * ```kotlin
 * SecondaryButton(
 *     onClick = { goBack() },
 *     leadingIcon = { Icon(YallaIcons.ArrowLeft, contentDescription = null) },
 * ) {
 *     Text("Go Back")
 * }
 * ```
 *
 * @param onClick Called when this button is clicked. Not called when disabled or loading.
 * @param modifier [Modifier] applied to the root container.
 * @param enabled Controls the enabled state. When `false`, the button appears visually
 *   disabled and does not respond to user input.
 * @param loading When `true`, shows a platform-native loading indicator instead of content.
 *   The button is not clickable while loading.
 * @param colors [SecondaryButtonColors] that resolve colors for different states.
 *   See [SecondaryButtonDefaults.colors].
 * @param dimens [SecondaryButtonDimens] that define dimensions and shape.
 *   See [SecondaryButtonDefaults.dimens].
 * @param leadingIcon Optional composable displayed before the content.
 * @param trailingIcon Optional composable displayed after the content.
 * @param content The button content, typically a [Text].
 *
 * @see PrimaryButton
 * @see TextButton
 * @see SecondaryButtonDefaults
 */
@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    colors: SecondaryButtonColors = SecondaryButtonDefaults.colors(),
    dimens: SecondaryButtonDimens = SecondaryButtonDefaults.dimens(),
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
 * Default configuration values for [SecondaryButton].
 *
 * Provides theme-aware [colors] and standard [dimens] that can be individually overridden.
 */
object SecondaryButtonDefaults {
    /** Default minimum button height. */
    val MinHeight = 60.dp

    /** Default content padding. */
    val ContentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)

    /** Default button shape. */
    val Shape: Shape = RoundedCornerShape(16.dp)

    /**
     * Creates [SecondaryButtonColors] with theme-aware defaults.
     *
     * @param containerColor Background color when enabled.
     * @param contentColor Text/icon color when enabled.
     * @param disabledContainerColor Background color when disabled.
     * @param disabledContentColor Text/icon color when disabled.
     */
    @Composable
    fun colors(
        containerColor: Color = System.color.button.tertiary,
        contentColor: Color = System.color.background.base,
        disabledContainerColor: Color = System.color.button.disabled,
        disabledContentColor: Color = System.color.text.white,
    ): SecondaryButtonColors = SecondaryButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )

    /**
     * Creates [SecondaryButtonDimens] with standard values.
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
    ): SecondaryButtonDimens = SecondaryButtonDimens(
        minHeight = minHeight,
        contentPadding = contentPadding,
        shape = shape,
        iconSpacing = iconSpacing,
    )
}

@Preview
@Composable
private fun SecondaryButtonPreview() {
    Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
        SecondaryButton(onClick = {}) {
            Text("Cancel")
        }
    }
}

@Preview
@Composable
private fun SecondaryButtonLoadingPreview() {
    Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
        SecondaryButton(onClick = {}, loading = true) {
            Text("Loading")
        }
    }
}
