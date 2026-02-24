package uz.yalla.primitives.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.primitives.button.SupportButtonDefaults.colors
import uz.yalla.primitives.button.SupportButtonDefaults.dimens
import uz.yalla.primitives.button.SupportButtonDefaults.style
import uz.yalla.resources.Res
import uz.yalla.resources.ic_headset

/**
 * State for [SupportButton].
 *
 * @property text Button label text.
 * @property icon Icon painter shown above the text.
 * @property enabled Whether button is clickable.
 */
data class SupportButtonState(
    val text: String,
    val icon: Painter,
    val enabled: Boolean = true,
)

/**
 * Outlined support action button with top icon and centered label.
 *
 * Matches the design variant with 24dp icon, 14sp label, and 60dp height.
 *
 * ## Usage
 *
 * ```kotlin
 * SupportButton(
 *     state = SupportButtonState(
 *         text = "Help",
 *         icon = painterResource(Res.drawable.ic_headset),
 *     ),
 *     onClick = onSupportClick,
 * )
 * ```
 *
 * @param state Button state containing text, icon, and enabled status.
 * @param onClick Invoked on click.
 * @param modifier Applied to button container.
 * @param colors Color configuration, defaults to [SupportButtonDefaults.colors].
 * @param style Text style configuration, defaults to [SupportButtonDefaults.style].
 * @param dimens Dimension configuration, defaults to [SupportButtonDefaults.dimens].
 */
@Composable
fun SupportButton(
    state: SupportButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SupportButtonDefaults.SupportButtonColors = colors(),
    style: SupportButtonDefaults.SupportButtonStyle = style(),
    dimens: SupportButtonDefaults.SupportButtonDimens = dimens(),
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = state.enabled,
        shape = dimens.shape,
        color = colors.container(state.enabled),
        border =
            BorderStroke(
                width = dimens.borderWidth,
                color = colors.border(state.enabled),
            ),
    ) {
        Column(
            modifier = Modifier.padding(dimens.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(
                    space = dimens.iconLabelSpacing,
                    alignment = Alignment.CenterVertically,
                ),
        ) {
            Icon(
                painter = state.icon,
                contentDescription = null,
                tint = colors.icon(state.enabled),
                modifier = Modifier.size(dimens.iconSize),
            )

            Text(
                text = state.text,
                color = colors.text(state.enabled),
                style = style.label,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Default configuration values for [SupportButton].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object SupportButtonDefaults {
    /**
     * Color configuration for [SupportButton].
     *
     * @param container Background color when enabled.
     * @param border Border color when enabled.
     * @param text Label color when enabled.
     * @param icon Icon tint when enabled.
     * @param disabledContainer Background color when disabled.
     * @param disabledBorder Border color when disabled.
     * @param disabledText Label color when disabled.
     * @param disabledIcon Icon tint when disabled.
     */
    data class SupportButtonColors(
        val container: Color,
        val border: Color,
        val text: Color,
        val icon: Color,
        val disabledContainer: Color,
        val disabledBorder: Color,
        val disabledText: Color,
        val disabledIcon: Color,
    ) {
        fun container(enabled: Boolean): Color = if (enabled) container else disabledContainer

        fun border(enabled: Boolean): Color = if (enabled) border else disabledBorder

        fun text(enabled: Boolean): Color = if (enabled) text else disabledText

        fun icon(enabled: Boolean): Color = if (enabled) icon else disabledIcon
    }

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        border: Color = System.color.borderDisabled,
        text: Color = System.color.textBase,
        icon: Color = System.color.iconBase,
        disabledContainer: Color = Color.Transparent,
        disabledBorder: Color = System.color.borderDisabled.copy(alpha = 0.5f),
        disabledText: Color = System.color.textSubtle,
        disabledIcon: Color = System.color.iconSubtle,
    ) = SupportButtonColors(
        container = container,
        border = border,
        text = text,
        icon = icon,
        disabledContainer = disabledContainer,
        disabledBorder = disabledBorder,
        disabledText = disabledText,
        disabledIcon = disabledIcon,
    )

    /**
     * Text style configuration for [SupportButton].
     *
     * @param label Style applied to the label text.
     */
    data class SupportButtonStyle(
        val label: TextStyle,
    )

    @Composable
    fun style(label: TextStyle = System.font.body.small.medium) =
        SupportButtonStyle(
            label = label,
        )

    /**
     * Dimension configuration for [SupportButton].
     *
     * @param shape Button shape.
     * @param borderWidth Border width.
     * @param contentPadding Content insets.
     * @param iconSize Icon size.
     * @param iconLabelSpacing Spacing between icon and label.
     */
    data class SupportButtonDimens(
        val shape: Shape,
        val borderWidth: Dp,
        val contentPadding: PaddingValues,
        val iconSize: Dp,
        val iconLabelSpacing: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        borderWidth: Dp = 1.dp,
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        iconSize: Dp = 24.dp,
        iconLabelSpacing: Dp = 4.dp,
    ) = SupportButtonDimens(
        shape = shape,
        borderWidth = borderWidth,
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconLabelSpacing = iconLabelSpacing,
    )
}

@Preview
@Composable
private fun SupportButtonPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp),
    ) {
        SupportButton(
            state =
                SupportButtonState(
                    text = "Помощь",
                    icon = painterResource(Res.drawable.ic_headset),
                ),
            onClick = {},
            modifier = Modifier.width(362.dp),
        )
    }
}
