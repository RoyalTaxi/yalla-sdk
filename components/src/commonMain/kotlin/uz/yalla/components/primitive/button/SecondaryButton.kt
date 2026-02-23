package uz.yalla.components.primitive.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.components.model.common.ButtonSize
import uz.yalla.design.theme.System
import uz.yalla.platform.indicator.NativeLoadingIndicator

/**
 * State for [SecondaryButton] component.
 *
 * Bundles button content and behavior properties for simplified component usage.
 *
 * @property text Button label.
 * @property enabled When false, button appears disabled and is not clickable.
 * @property loading When true, shows spinner and disables interaction.
 * @property size Button size variant.
 */
data class SecondaryButtonState(
    val text: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val size: ButtonSize = ButtonSize.Medium
)

/**
 * Secondary action button for alternative or less prominent actions.
 *
 * Use for secondary actions on a screen, such as "Cancel", "Skip", or "Later".
 * For the main action, use [PrimaryButton].
 *
 * ## Usage
 *
 * ```kotlin
 * SecondaryButton(
 *     state = SecondaryButtonState(text = "Cancel"),
 *     onClick = onCancel,
 * )
 * ```
 *
 * @param state Button state containing text, enabled, loading, and size.
 * @param onClick Invoked on click. Not called when disabled or loading.
 * @param modifier Applied to button container.
 * @param colors Color configuration, defaults to [SecondaryButtonDefaults.colors].
 * @param style Text style configuration, defaults to [SecondaryButtonDefaults.style].
 * @param dimens Dimension configuration, defaults to [SecondaryButtonDefaults.dimens].
 * @param leadingIcon Optional icon before text.
 * @param trailingIcon Optional icon after text.
 *
 * @see PrimaryButton for primary actions
 * @see SecondaryButtonDefaults for default values
 */
@Composable
fun SecondaryButton(
    state: SecondaryButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SecondaryButtonDefaults.SecondaryButtonColors = SecondaryButtonDefaults.colors(),
    style: SecondaryButtonDefaults.SecondaryButtonStyle = SecondaryButtonDefaults.style(),
    dimens: SecondaryButtonDefaults.SecondaryButtonDimens = SecondaryButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val isInteractive = state.enabled && !state.loading
    val containerColor = colors.containerColor(isInteractive)
    val contentColor = colors.contentColor(isInteractive)

    Surface(
        onClick = onClick,
        modifier =
            modifier.defaultMinSize(
                minWidth = dimens.minWidth(state.size),
                minHeight = dimens.minHeight(state.size),
            ),
        enabled = isInteractive,
        shape = dimens.shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(dimens.contentPadding(state.size)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.loading) {
                NativeLoadingIndicator(
                    modifier = Modifier.size(dimens.iconSize),
                    color = LocalContentColor.current,
                    backgroundColor = containerColor,
                )
            } else {
                leadingIcon?.let { icon ->
                    Box(
                        modifier = Modifier.size(dimens.iconSize),
                        contentAlignment = Alignment.Center,
                    ) { icon() }
                    Spacer(Modifier.width(dimens.iconSpacing))
                }

                Text(
                    text = state.text,
                    style = style.textStyle(state.size),
                )

                trailingIcon?.let { icon ->
                    Spacer(Modifier.width(dimens.iconSpacing))
                    Box(
                        modifier = Modifier.size(dimens.iconSize),
                        contentAlignment = Alignment.Center,
                    ) { icon() }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SecondaryButtonPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        SecondaryButton(
            state = SecondaryButtonState(text = "Cancel"),
            onClick = {},
        )
    }
}

/**
 * Default configuration values for [SecondaryButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object SecondaryButtonDefaults {
    /**
     * Color configuration for [SecondaryButton].
     *
     * @param container Background color when enabled.
     * @param content Text and icon color when enabled.
     * @param disabledContainer Background color when disabled.
     * @param disabledContent Text and icon color when disabled.
     */
    data class SecondaryButtonColors(
        val container: Color,
        val content: Color,
        val disabledContainer: Color,
        val disabledContent: Color
    ) {
        fun containerColor(enabled: Boolean): Color = if (enabled) container else disabledContainer

        fun contentColor(enabled: Boolean): Color = if (enabled) content else disabledContent
    }

    @Composable
    fun colors(
        container: Color = System.color.buttonTertiary,
        content: Color = System.color.backgroundBase,
        disabledContainer: Color = System.color.buttonDisabled,
        disabledContent: Color = System.color.textWhite
    ) = SecondaryButtonColors(
        container = container,
        content = content,
        disabledContainer = disabledContainer,
        disabledContent = disabledContent
    )

    /**
     * Text style configuration for [SecondaryButton].
     *
     * @param small Style for small button size.
     * @param medium Style for medium button size.
     * @param large Style for large button size.
     */
    data class SecondaryButtonStyle(
        val small: TextStyle,
        val medium: TextStyle,
        val large: TextStyle
    ) {
        fun textStyle(size: ButtonSize): TextStyle =
            when (size) {
                ButtonSize.Small -> small
                ButtonSize.Medium -> medium
                ButtonSize.Large -> large
            }
    }

    @Composable
    fun style(
        small: TextStyle = System.font.body.small.medium,
        medium: TextStyle = System.font.body.base.medium,
        large: TextStyle = System.font.body.large.medium
    ) = SecondaryButtonStyle(
        small = small,
        medium = medium,
        large = large
    )

    /**
     * Dimension configuration for [SecondaryButton].
     *
     * @param shape Button shape.
     * @param iconSize Icon dimensions for leading/trailing slots.
     * @param iconSpacing Spacing between icon and text.
     * @param smallMinWidth Minimum width for small size.
     * @param smallMinHeight Minimum height for small size.
     * @param smallContentPadding Content padding for small size.
     * @param mediumMinWidth Minimum width for medium size.
     * @param mediumMinHeight Minimum height for medium size.
     * @param mediumContentPadding Content padding for medium size.
     * @param largeMinWidth Minimum width for large size.
     * @param largeMinHeight Minimum height for large size.
     * @param largeContentPadding Content padding for large size.
     */
    data class SecondaryButtonDimens(
        val shape: Shape,
        val iconSize: Dp,
        val iconSpacing: Dp,
        val smallMinWidth: Dp,
        val smallMinHeight: Dp,
        val smallContentPadding: PaddingValues,
        val mediumMinWidth: Dp,
        val mediumMinHeight: Dp,
        val mediumContentPadding: PaddingValues,
        val largeMinWidth: Dp,
        val largeMinHeight: Dp,
        val largeContentPadding: PaddingValues
    ) {
        fun minWidth(size: ButtonSize): Dp =
            when (size) {
                ButtonSize.Small -> smallMinWidth
                ButtonSize.Medium -> mediumMinWidth
                ButtonSize.Large -> largeMinWidth
            }

        fun minHeight(size: ButtonSize): Dp =
            when (size) {
                ButtonSize.Small -> smallMinHeight
                ButtonSize.Medium -> mediumMinHeight
                ButtonSize.Large -> largeMinHeight
            }

        fun contentPadding(size: ButtonSize): PaddingValues =
            when (size) {
                ButtonSize.Small -> smallContentPadding
                ButtonSize.Medium -> mediumContentPadding
                ButtonSize.Large -> largeContentPadding
            }
    }

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        iconSize: Dp = 20.dp,
        iconSpacing: Dp = 8.dp,
        smallMinWidth: Dp = 80.dp,
        smallMinHeight: Dp = 40.dp,
        smallContentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        mediumMinWidth: Dp = 120.dp,
        mediumMinHeight: Dp = 60.dp,
        mediumContentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        largeMinWidth: Dp = 160.dp,
        largeMinHeight: Dp = 60.dp,
        largeContentPadding: PaddingValues = PaddingValues(horizontal = 32.dp, vertical = 18.dp)
    ) = SecondaryButtonDimens(
        shape = shape,
        iconSize = iconSize,
        iconSpacing = iconSpacing,
        smallMinWidth = smallMinWidth,
        smallMinHeight = smallMinHeight,
        smallContentPadding = smallContentPadding,
        mediumMinWidth = mediumMinWidth,
        mediumMinHeight = mediumMinHeight,
        mediumContentPadding = mediumContentPadding,
        largeMinWidth = largeMinWidth,
        largeMinHeight = largeMinHeight,
        largeContentPadding = largeContentPadding
    )
}
