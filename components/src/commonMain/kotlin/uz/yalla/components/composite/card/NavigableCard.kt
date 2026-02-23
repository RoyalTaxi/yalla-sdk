package uz.yalla.components.composite.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Default configuration values for [NavigableCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object NavigableCardDefaults {
    /**
     * Color configuration for [NavigableCard].
     *
     * @param container Card background color.
     * @param border Border color.
     * @param arrow Arrow icon tint.
     * @param disabledContainer Disabled state background.
     * @param disabledBorder Disabled state border.
     * @param disabledArrow Disabled state arrow tint.
     */
    data class NavigableCardColors(
        val container: Color,
        val border: Color,
        val arrow: Color,
        val disabledContainer: Color,
        val disabledBorder: Color,
        val disabledArrow: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        border: Color = System.color.borderDisabled,
        arrow: Color = System.color.iconBase,
        disabledContainer: Color = Color.Transparent,
        disabledBorder: Color = System.color.borderDisabled.copy(alpha = 0.5f),
        disabledArrow: Color = System.color.iconDisabled,
    ) = NavigableCardColors(
        container = container,
        border = border,
        arrow = arrow,
        disabledContainer = disabledContainer,
        disabledBorder = disabledBorder,
        disabledArrow = disabledArrow,
    )

    /**
     * Dimension configuration for [NavigableCard].
     *
     * @param radius Corner radius.
     * @param borderWidth Border stroke width.
     * @param arrowSize Arrow icon size.
     * @param iconSpacing Spacing between icon and content.
     * @param horizontalPadding Horizontal content padding.
     * @param verticalPadding Vertical content padding.
     */
    data class NavigableCardDimens(
        val radius: Dp,
        val borderWidth: Dp,
        val arrowSize: Dp,
        val iconSpacing: Dp,
        val horizontalPadding: Dp,
        val verticalPadding: Dp,
    )

    @Composable
    fun dimens(
        radius: Dp = 16.dp,
        borderWidth: Dp = 1.dp,
        arrowSize: Dp = 24.dp,
        iconSpacing: Dp = 8.dp,
        horizontalPadding: Dp = 16.dp,
        verticalPadding: Dp = 18.dp,
    ) = NavigableCardDimens(
        radius = radius,
        borderWidth = borderWidth,
        arrowSize = arrowSize,
        iconSpacing = iconSpacing,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
    )
}

/**
 * Navigable card with forward arrow indicating clickable navigation.
 *
 * Use for list items that navigate to another screen.
 *
 * @param onClick Called when card is clicked.
 * @param modifier Applied to card.
 * @param leadingIcon Optional icon before content.
 * @param enabled Whether card is enabled.
 * @param colors Color configuration, defaults to [NavigableCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [NavigableCardDefaults.dimens].
 * @param content Card content, receives modifier for weight.
 */
@Composable
fun NavigableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: NavigableCardDefaults.NavigableCardColors = NavigableCardDefaults.colors(),
    dimens: NavigableCardDefaults.NavigableCardDimens = NavigableCardDefaults.dimens(),
    content: @Composable (Modifier) -> Unit,
) {
    val shape: Shape = RoundedCornerShape(dimens.radius)

    Card(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
                disabledContainerColor = colors.disabledContainer,
            ),
        border =
            BorderStroke(
                width = dimens.borderWidth,
                color = if (enabled) colors.border else colors.disabledBorder,
            ),
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal = dimens.horizontalPadding,
                    vertical = dimens.verticalPadding,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(dimens.iconSpacing))
            }

            content(Modifier.weight(1f))

            Spacer(Modifier.width(dimens.iconSpacing))

            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = null,
                tint = if (enabled) colors.arrow else colors.disabledArrow,
                modifier = Modifier.size(dimens.arrowSize),
            )
        }
    }
}

@Preview
@Composable
private fun NavigableCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        NavigableCard(
            onClick = {},
            content = { modifier ->
                Text(
                    text = "Profile Settings",
                    modifier = modifier,
                )
            },
        )
    }
}
