package uz.yalla.composites.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
 * Color configuration for [AddressCard].
 *
 * @param container Card background color.
 * @param title Title text color.
 * @param subtitle Subtitle text color.
 * @param footer Footer text color.
 * @since 0.0.1
 */
@Immutable
data class AddressCardColors(
    val container: Color,
    val title: Color,
    val subtitle: Color,
    val footer: Color,
)

/**
 * Dimension configuration for [AddressCard].
 *
 * @param shape Card corner shape.
 * @param maxWidth Maximum card width.
 * @param height Fixed card height.
 * @param contentPadding Padding inside the card.
 * @param titleIconSpacing Spacing between leading icon and title.
 * @param contentSpacing Spacing between title row and subtitle.
 * @since 0.0.1
 */
@Immutable
data class AddressCardDimens(
    val shape: Shape,
    val maxWidth: Dp,
    val height: Dp,
    val contentPadding: PaddingValues,
    val titleIconSpacing: Dp,
    val contentSpacing: Dp,
)

/**
 * Default configuration values for [AddressCard].
 *
 * @since 0.0.1
 */
object AddressCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        title: Color = System.color.text.base,
        subtitle: Color = System.color.text.base,
        footer: Color = System.color.text.subtle,
    ): AddressCardColors = AddressCardColors(
        container = container,
        title = title,
        subtitle = subtitle,
        footer = footer,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        maxWidth: Dp = 248.dp,
        height: Dp = 120.dp,
        contentPadding: PaddingValues = PaddingValues(16.dp),
        titleIconSpacing: Dp = 8.dp,
        contentSpacing: Dp = 8.dp,
    ): AddressCardDimens = AddressCardDimens(
        shape = shape,
        maxWidth = maxWidth,
        height = height,
        contentPadding = contentPadding,
        titleIconSpacing = titleIconSpacing,
        contentSpacing = contentSpacing,
    )
}

/**
 * Fixed-size address card for saved places (home, work, favorites).
 *
 * Displays a compact card with width-constrained layout containing a title row
 * (with optional leading icon), subtitle, and footer. The card has a fixed height
 * and max width, making it suitable for horizontal scrollable lists.
 *
 * ## Usage
 *
 * ```kotlin
 * AddressCard(
 *     onClick = { navigateToPlace(place) },
 *     leadingIcon = { Icon(YallaIcons.Home, null) },
 *     subtitle = { Text(place.address, style = System.font.body.caption) },
 *     footer = { Text(place.distance, style = System.font.body.caption) },
 * ) {
 *     Text(place.name, style = System.font.title.base)
 * }
 * ```
 *
 * @param onClick Called when the card is clicked.
 * @param modifier Applied to the root card.
 * @param colors Color configuration, defaults to [AddressCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [AddressCardDefaults.dimens].
 * @param leadingIcon Optional icon displayed before the title.
 * @param subtitle Optional secondary content below the title row.
 * @param footer Optional footer content at the bottom of the card.
 * @param title Primary content displayed in the title row.
 *
 * @see AddressCardDefaults
 * @since 0.0.1
 */
@Composable
fun AddressCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: AddressCardColors = AddressCardDefaults.colors(),
    dimens: AddressCardDimens = AddressCardDefaults.dimens(),
    leadingIcon: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(containerColor = colors.container),
        modifier = modifier
            .widthIn(max = dimens.maxWidth)
            .height(dimens.height),
    ) {
        Column(modifier = Modifier.padding(dimens.contentPadding)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon?.let {
                    it()
                    Spacer(modifier = Modifier.width(dimens.titleIconSpacing))
                }
                title()
            }

            subtitle?.let {
                Spacer(modifier = Modifier.height(dimens.contentSpacing))
                it()
            }

            Spacer(modifier = Modifier.weight(1f))

            footer?.invoke()
        }
    }
}
