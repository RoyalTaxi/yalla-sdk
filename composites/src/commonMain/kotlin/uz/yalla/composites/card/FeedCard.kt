package uz.yalla.composites.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [FeedCard].
 *
 * @param container Card background color.
 * @param border Border color.
 * @param indicator Highlight indicator color (shown when [FeedCard.isHighlighted] is true).
 * @since 0.0.5-alpha11
 */
@Immutable
data class FeedCardColors(
    val container: Color,
    val border: Color,
    val indicator: Color,
)

/**
 * Dimension configuration for [FeedCard].
 *
 * @param shape Card corner shape.
 * @param borderWidth Border stroke width.
 * @param contentPadding Padding around content.
 * @param indicatorHeight Height of the highlight indicator bar.
 * @param indicatorWidth Width of the highlight indicator bar.
 * @param indicatorRadius Corner radius of the indicator bar.
 * @since 0.0.5-alpha11
 */
@Immutable
data class FeedCardDimens(
    val shape: Shape,
    val borderWidth: Dp,
    val contentPadding: PaddingValues,
    val indicatorHeight: Dp,
    val indicatorWidth: Dp,
    val indicatorRadius: Dp,
)

/**
 * Default configuration values for [FeedCard].
 *
 * @since 0.0.5-alpha11
 */
object FeedCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        border: Color = System.color.border.disabled,
        indicator: Color = System.color.background.brand,
    ): FeedCardColors = FeedCardColors(
        container = container,
        border = border,
        indicator = indicator,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(8.dp),
        borderWidth: Dp = 1.dp,
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        indicatorHeight: Dp = 64.dp,
        indicatorWidth: Dp = 4.dp,
        indicatorRadius: Dp = 2.dp,
    ): FeedCardDimens = FeedCardDimens(
        shape = shape,
        borderWidth = borderWidth,
        contentPadding = contentPadding,
        indicatorHeight = indicatorHeight,
        indicatorWidth = indicatorWidth,
        indicatorRadius = indicatorRadius,
    )
}

/**
 * Card with an optional highlight indicator for feed/notification items.
 *
 * Built on [ContentCard] with a left-edge indicator bar for unread/highlighted state.
 *
 * ## Usage
 *
 * ```kotlin
 * FeedCard(
 *     onClick = { openNotification() },
 *     isHighlighted = !notification.isRead,
 * ) {
 *     Column {
 *         Row {
 *             Text(notification.title, style = System.font.body.base.bold, modifier = Modifier.weight(1f))
 *             Text(notification.dateTime, style = System.font.body.caption)
 *         }
 *         HorizontalDivider()
 *         Text(notification.content, maxLines = 3, overflow = TextOverflow.Ellipsis)
 *     }
 * }
 * ```
 *
 * @param onClick Called when the card is clicked.
 * @param modifier Applied to the root card.
 * @param isHighlighted Whether to show the highlight indicator bar.
 * @param colors Color configuration, defaults to [FeedCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [FeedCardDefaults.dimens].
 * @param content Card content — caller controls the layout.
 *
 * @see ContentCard
 * @see FeedCardDefaults
 * @since 0.0.5-alpha11
 */
@Composable
fun FeedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    colors: FeedCardColors = FeedCardDefaults.colors(),
    dimens: FeedCardDimens = FeedCardDefaults.dimens(),
    content: @Composable () -> Unit,
) {
    ContentCard(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(dimens.borderWidth, colors.border),
        colors = ContentCardDefaults.colors(container = colors.container),
        dimens = ContentCardDefaults.dimens(
            shape = dimens.shape,
            contentPadding = PaddingValues(0.dp),
        ),
    ) {
        Box {
            if (isHighlighted) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .height(dimens.indicatorHeight)
                        .width(dimens.indicatorWidth)
                        .clip(
                            RoundedCornerShape(
                                topEnd = dimens.indicatorRadius,
                                bottomEnd = dimens.indicatorRadius,
                            ),
                        )
                        .background(colors.indicator),
                )
            }

            Box(modifier = Modifier.padding(dimens.contentPadding)) {
                content()
            }
        }
    }
}
