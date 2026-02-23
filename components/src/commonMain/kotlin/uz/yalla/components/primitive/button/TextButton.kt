package uz.yalla.components.primitive.button

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.components.model.common.ButtonSize
import uz.yalla.design.theme.System
import uz.yalla.platform.indicator.NativeLoadingIndicator

/**
 * State for [TextButton] component.
 *
 * @property text Button label.
 * @property enabled When false, button is disabled.
 * @property loading When true, shows spinner.
 * @property size Button size variant.
 */
data class TextButtonState(
    val text: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val size: ButtonSize = ButtonSize.Medium
)

/**
 * Text-only button for low-emphasis actions.
 *
 * Use for tertiary actions, links, or actions that should be less prominent.
 *
 * ## Usage
 *
 * ```kotlin
 * TextButton(
 *     state = TextButtonState(text = "Learn More"),
 *     onClick = onLearnMore,
 * )
 * ```
 *
 * @param state Button state containing text, enabled, loading, and size.
 * @param onClick Invoked on click.
 * @param modifier Applied to button.
 * @param colors Color configuration, defaults to [TextButtonDefaults.colors].
 * @param style Text style configuration, defaults to [TextButtonDefaults.style].
 * @param dimens Dimension configuration, defaults to [TextButtonDefaults.dimens].
 * @param leadingIcon Optional icon before text.
 * @param trailingIcon Optional icon after text.
 *
 * @see PrimaryButton for primary actions
 * @see TextButtonDefaults for default values
 */
@Composable
fun TextButton(
    state: TextButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TextButtonDefaults.TextButtonColors = TextButtonDefaults.colors(),
    style: TextButtonDefaults.TextButtonStyle = TextButtonDefaults.style(),
    dimens: TextButtonDefaults.TextButtonDimens = TextButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val isInteractive = state.enabled && !state.loading

    Surface(
        onClick = onClick,
        modifier =
            modifier.defaultMinSize(
                minWidth = dimens.minWidth(state.size),
                minHeight = dimens.minHeight(state.size),
            ),
        enabled = isInteractive,
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        contentColor = colors.contentColor(isInteractive),
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
                )
            } else {
                leadingIcon?.let { icon ->
                    Box(Modifier.size(dimens.iconSize), Alignment.Center) { icon() }
                    Spacer(Modifier.width(dimens.iconSpacing))
                }

                Text(text = state.text, style = style.forSize(state.size))

                trailingIcon?.let { icon ->
                    Spacer(Modifier.width(dimens.iconSpacing))
                    Box(Modifier.size(dimens.iconSize), Alignment.Center) { icon() }
                }
            }
        }
    }
}

/**
 * Default configuration values for [TextButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object TextButtonDefaults {
    /**
     * Color configuration for [TextButton].
     *
     * @param content Text/icon color when enabled.
     * @param disabledContent Text/icon color when disabled.
     */
    data class TextButtonColors(
        val content: Color,
        val disabledContent: Color,
    ) {
        fun contentColor(enabled: Boolean): Color = if (enabled) content else disabledContent
    }

    @Composable
    fun colors(
        content: Color = System.color.textLink,
        disabledContent: Color = System.color.textBase,
    ) = TextButtonColors(
        content = content,
        disabledContent = disabledContent,
    )

    /**
     * Text style configuration for [TextButton].
     *
     * @param small Style for small size.
     * @param medium Style for medium size.
     * @param large Style for large size.
     */
    data class TextButtonStyle(
        val small: TextStyle,
        val medium: TextStyle,
        val large: TextStyle,
    ) {
        fun forSize(size: ButtonSize): TextStyle =
            when (size) {
                ButtonSize.Small -> small
                ButtonSize.Medium -> medium
                ButtonSize.Large -> large
            }
    }

    @Composable
    fun style(
        small: TextStyle = System.font.body.small.regular,
        medium: TextStyle = System.font.body.small.regular,
        large: TextStyle = System.font.body.small.regular,
    ) = TextButtonStyle(
        small = small,
        medium = medium,
        large = large,
    )

    /**
     * Dimension configuration for [TextButton].
     *
     * @param smallMinWidth Minimum width for small size.
     * @param mediumMinWidth Minimum width for medium size.
     * @param largeMinWidth Minimum width for large size.
     * @param smallMinHeight Minimum height for small size.
     * @param mediumMinHeight Minimum height for medium size.
     * @param largeMinHeight Minimum height for large size.
     * @param smallContentPadding Content padding for small size.
     * @param mediumContentPadding Content padding for medium size.
     * @param largeContentPadding Content padding for large size.
     * @param iconSize Size of icons.
     * @param iconSpacing Spacing between icon and text.
     */
    data class TextButtonDimens(
        val smallMinWidth: Dp,
        val mediumMinWidth: Dp,
        val largeMinWidth: Dp,
        val smallMinHeight: Dp,
        val mediumMinHeight: Dp,
        val largeMinHeight: Dp,
        val smallContentPadding: PaddingValues,
        val mediumContentPadding: PaddingValues,
        val largeContentPadding: PaddingValues,
        val iconSize: Dp,
        val iconSpacing: Dp,
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
        smallMinWidth: Dp = 60.dp,
        mediumMinWidth: Dp = 80.dp,
        largeMinWidth: Dp = 100.dp,
        smallMinHeight: Dp = 36.dp,
        mediumMinHeight: Dp = 44.dp,
        largeMinHeight: Dp = 52.dp,
        smallContentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        mediumContentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        largeContentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
        iconSize: Dp = 20.dp,
        iconSpacing: Dp = 8.dp,
    ) = TextButtonDimens(
        smallMinWidth = smallMinWidth,
        mediumMinWidth = mediumMinWidth,
        largeMinWidth = largeMinWidth,
        smallMinHeight = smallMinHeight,
        mediumMinHeight = mediumMinHeight,
        largeMinHeight = largeMinHeight,
        smallContentPadding = smallContentPadding,
        mediumContentPadding = mediumContentPadding,
        largeContentPadding = largeContentPadding,
        iconSize = iconSize,
        iconSpacing = iconSpacing,
    )
}

@Preview
@Composable
private fun TextButtonPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        TextButton(
            state = TextButtonState(text = "Learn More"),
            onClick = {},
        )
    }
}
