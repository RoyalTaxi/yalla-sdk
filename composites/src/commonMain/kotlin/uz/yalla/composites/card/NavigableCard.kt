package uz.yalla.composites.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [NavigableCard].
 *
 * @param container Card background color.
 * @param border Border color when enabled.
 * @param arrow Arrow icon tint when enabled.
 * @param disabledContainer Background when disabled.
 * @param disabledBorder Border color when disabled.
 * @param disabledArrow Arrow tint when disabled.
 * @since 0.0.5-alpha11
 */
@Immutable
data class NavigableCardColors(
    val container: Color,
    val border: Color,
    val arrow: Color,
    val disabledContainer: Color,
    val disabledBorder: Color,
    val disabledArrow: Color,
)

/**
 * Dimension configuration for [NavigableCard].
 *
 * @param shape Card corner shape.
 * @param borderWidth Border stroke width.
 * @param arrowSize Arrow icon size.
 * @param iconSpacing Spacing between leading icon and content, and content and arrow.
 * @param contentPadding Padding inside the card.
 * @since 0.0.5-alpha11
 */
@Immutable
data class NavigableCardDimens(
    val shape: Shape,
    val borderWidth: Dp,
    val arrowSize: Dp,
    val iconSpacing: Dp,
    val contentPadding: PaddingValues,
)

/**
 * Default configuration values for [NavigableCard].
 *
 * @since 0.0.1
 */
object NavigableCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = Color.Transparent,
        border: Color = System.color.border.disabled,
        arrow: Color = System.color.icon.base,
        disabledContainer: Color = Color.Transparent,
        disabledBorder: Color = System.color.border.disabled.copy(alpha = 0.5f),
        disabledArrow: Color = System.color.icon.disabled,
    ): NavigableCardColors = NavigableCardColors(
        container = container,
        border = border,
        arrow = arrow,
        disabledContainer = disabledContainer,
        disabledBorder = disabledBorder,
        disabledArrow = disabledArrow,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        borderWidth: Dp = 1.dp,
        arrowSize: Dp = 24.dp,
        iconSpacing: Dp = 8.dp,
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
    ): NavigableCardDimens = NavigableCardDimens(
        shape = shape,
        borderWidth = borderWidth,
        arrowSize = arrowSize,
        iconSpacing = iconSpacing,
        contentPadding = contentPadding,
    )
}

/**
 * Navigable card with forward arrow indicating clickable navigation.
 *
 * Built on [ContentCard] with a trailing chevron icon.
 *
 * ## Usage
 *
 * ```kotlin
 * NavigableCard(onClick = { navigateToSettings() }) { modifier ->
 *     Text("Profile Settings", modifier = modifier)
 * }
 * ```
 *
 * ## With Leading Icon
 *
 * ```kotlin
 * NavigableCard(
 *     onClick = { addCard() },
 *     leadingIcon = { Icon(YallaIcons.Plus, null) },
 * ) { modifier ->
 *     Text("Add Card", modifier = modifier)
 * }
 * ```
 *
 * @param onClick Called when the card is clicked.
 * @param modifier Applied to the root card.
 * @param enabled Whether the card responds to clicks.
 * @param colors Color configuration, defaults to [NavigableCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [NavigableCardDefaults.dimens].
 * @param leadingIcon Optional icon before content.
 * @param content Card content. Receives a [Modifier] with `weight(1f)` for proper sizing.
 *
 * @see ContentCard
 * @see NavigableCardDefaults
 * @since 0.0.1
 */
@Composable
fun NavigableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: NavigableCardColors = NavigableCardDefaults.colors(),
    dimens: NavigableCardDimens = NavigableCardDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit,
) {
    ContentCard(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        border = BorderStroke(
            width = dimens.borderWidth,
            color = if (enabled) colors.border else colors.disabledBorder,
        ),
        colors = ContentCardDefaults.colors(
            container = colors.container,
            disabledContainer = colors.disabledContainer,
        ),
        dimens = ContentCardDefaults.dimens(
            shape = dimens.shape,
            contentPadding = dimens.contentPadding,
        ),
    ) {
        Row(
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
