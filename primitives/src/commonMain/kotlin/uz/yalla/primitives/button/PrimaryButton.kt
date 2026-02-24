package uz.yalla.primitives.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import uz.yalla.design.theme.System
import uz.yalla.platform.indicator.NativeLoadingIndicator
import uz.yalla.primitives.model.ButtonSize

/**
 * State for [PrimaryButton] component.
 *
 * Bundles button content and behavior properties for simplified component usage.
 *
 * @property text Button label. Keep concise (1-3 words).
 * @property enabled When false, button appears disabled and is not clickable.
 * @property loading When true, shows spinner and disables interaction.
 * @property size Button size variant.
 */
data class PrimaryButtonState(
    val text: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
    val size: ButtonSize = ButtonSize.Medium
)

/**
 * Primary action button for main user interactions.
 *
 * Use for the single most important action on a screen, such as "Confirm",
 * "Continue", or "Submit". For secondary actions, use [SecondaryButton].
 *
 * ## Usage
 *
 * ```kotlin
 * PrimaryButton(
 *     state = PrimaryButtonState(text = "Confirm Order"),
 *     onClick = viewModel::confirmOrder,
 * )
 * ```
 *
 * ## With Loading State
 *
 * ```kotlin
 * PrimaryButton(
 *     state = PrimaryButtonState(
 *         text = "Submit",
 *         loading = state.isLoading
 *     ),
 *     onClick = onSubmit,
 * )
 * ```
 *
 * ## With Icons
 *
 * ```kotlin
 * PrimaryButton(
 *     state = PrimaryButtonState(text = "Add to Cart"),
 *     onClick = onAddToCart,
 *     leadingIcon = { Icon(Icons.Default.Add, null) },
 * )
 * ```
 *
 * @param state Button state containing text, enabled, loading, and size.
 * @param onClick Invoked on click. Not called when disabled or loading.
 * @param modifier Applied to button container.
 * @param colors Color configuration, defaults to [PrimaryButtonDefaults.colors].
 * @param style Text style configuration, defaults to [PrimaryButtonDefaults.style].
 * @param dimens Dimension configuration, defaults to [PrimaryButtonDefaults.dimens].
 * @param leadingIcon Optional composable before text.
 * @param trailingIcon Optional composable after text.
 *
 * @see SecondaryButton for secondary actions
 * @see TextButton for low-emphasis actions
 * @see PrimaryButtonDefaults for default values
 */
@Composable
fun PrimaryButton(
    state: PrimaryButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: PrimaryButtonDefaults.PrimaryButtonColors = PrimaryButtonDefaults.colors(),
    style: PrimaryButtonDefaults.PrimaryButtonStyle = PrimaryButtonDefaults.style(),
    dimens: PrimaryButtonDefaults.PrimaryButtonDimens = PrimaryButtonDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val isInteractive = state.enabled && !state.loading
    val containerColor = colors.containerColor(isInteractive)
    val contentColor = colors.contentColor(isInteractive)

    ButtonContainer(
        onClick = onClick,
        modifier =
            modifier.defaultMinSize(
                minWidth = dimens.minWidth(state.size),
                minHeight = dimens.minHeight(state.size),
            ),
        enabled = isInteractive,
        shape = dimens.shape,
        containerColor = containerColor,
        contentColor = contentColor,
        contentPadding = dimens.contentPadding(state.size),
    ) {
        ButtonContent(
            text = state.text,
            textStyle = style.textStyle(state.size),
            loading = state.loading,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            dimens = dimens,
            indicatorBackgroundColor = containerColor,
        )
    }
}

/**
 * Container surface for button with click handling.
 */
@Composable
private fun ButtonContainer(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    contentPadding: PaddingValues,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/**
 * Button content with text, optional icons, and loading state.
 */
@Composable
private fun RowScope.ButtonContent(
    text: String,
    textStyle: TextStyle,
    loading: Boolean,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    dimens: PrimaryButtonDefaults.PrimaryButtonDimens,
    indicatorBackgroundColor: Color,
) {
    if (loading) {
        LoadingIndicator(
            dimens = dimens,
            backgroundColor = indicatorBackgroundColor,
        )
    } else {
        TextWithIcons(
            text = text,
            textStyle = textStyle,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            dimens = dimens,
        )
    }
}

/**
 * Text content with optional leading and trailing icons.
 */
@Composable
private fun TextWithIcons(
    text: String,
    textStyle: TextStyle,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    dimens: PrimaryButtonDefaults.PrimaryButtonDimens,
) {
    leadingIcon?.let { icon ->
        IconSlot(content = icon, dimens = dimens)
        Spacer(Modifier.width(dimens.iconSpacing))
    }

    Text(text = text, style = textStyle)

    trailingIcon?.let { icon ->
        Spacer(Modifier.width(dimens.iconSpacing))
        IconSlot(content = icon, dimens = dimens)
    }
}

/**
 * Sized container for button icons.
 */
@Composable
private fun IconSlot(
    content: @Composable () -> Unit,
    dimens: PrimaryButtonDefaults.PrimaryButtonDimens,
) {
    Box(
        modifier = Modifier.size(dimens.iconSize),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Loading spinner for button loading state.
 */
@Composable
private fun LoadingIndicator(
    dimens: PrimaryButtonDefaults.PrimaryButtonDimens,
    backgroundColor: Color,
) {
    NativeLoadingIndicator(
        modifier = Modifier.size(dimens.iconSize),
        color = LocalContentColor.current,
        backgroundColor = backgroundColor,
    )
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        PrimaryButton(
            state = PrimaryButtonState(text = "Continue"),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PrimaryButtonLoadingPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        PrimaryButton(
            state = PrimaryButtonState(text = "Loading", loading = true),
            onClick = {},
        )
    }
}

/**
 * Default configuration values for [PrimaryButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object PrimaryButtonDefaults {
    /**
     * Color configuration for [PrimaryButton].
     *
     * @param container Background color when enabled.
     * @param content Text and icon color when enabled.
     * @param disabledContainer Background color when disabled.
     * @param disabledContent Text and icon color when disabled.
     */
    data class PrimaryButtonColors(
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
        container: Color = System.color.buttonActive,
        content: Color = System.color.textWhite,
        disabledContainer: Color = System.color.buttonDisabled,
        disabledContent: Color = System.color.textWhite
    ) = PrimaryButtonColors(
        container = container,
        content = content,
        disabledContainer = disabledContainer,
        disabledContent = disabledContent
    )

    /**
     * Text style configuration for [PrimaryButton].
     *
     * @param small Style for small button size.
     * @param medium Style for medium button size.
     * @param large Style for large button size.
     */
    data class PrimaryButtonStyle(
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
    ) = PrimaryButtonStyle(
        small = small,
        medium = medium,
        large = large
    )

    /**
     * Dimension configuration for [PrimaryButton].
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
    data class PrimaryButtonDimens(
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
    ) = PrimaryButtonDimens(
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
