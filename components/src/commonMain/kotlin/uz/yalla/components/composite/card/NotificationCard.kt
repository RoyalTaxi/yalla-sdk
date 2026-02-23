package uz.yalla.components.composite.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * UI state for [NotificationCard].
 *
 * @param title Notification title.
 * @param dateTime Display timestamp.
 * @param content Notification body text.
 * @param isRead Whether notification has been read.
 */
data class NotificationCardState(
    val title: String,
    val dateTime: String,
    val content: String,
    val isRead: Boolean,
)

/**
 * Default configuration for [NotificationCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object NotificationCardDefaults {
    /**
     * Color configuration for [NotificationCard].
     *
     * @param container Background color.
     * @param border Border color.
     * @param title Title text color.
     * @param dateTime Date/time text color.
     * @param content Content text color.
     * @param divider Divider color.
     * @param unreadIndicator Unread indicator color.
     */
    data class NotificationCardColors(
        val container: Color,
        val border: Color,
        val title: Color,
        val dateTime: Color,
        val content: Color,
        val divider: Color,
        val unreadIndicator: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        border: Color = System.color.borderDisabled,
        title: Color = System.color.textBase,
        dateTime: Color = System.color.textBase,
        content: Color = System.color.textBase,
        divider: Color = System.color.borderDisabled,
        unreadIndicator: Color = System.color.backgroundBrandBase,
    ) = NotificationCardColors(
        container = container,
        border = border,
        title = title,
        dateTime = dateTime,
        content = content,
        divider = divider,
        unreadIndicator = unreadIndicator,
    )

    /**
     * Dimension configuration for [NotificationCard].
     *
     * @param radius Card corner radius.
     * @param borderWidth Border width.
     * @param horizontalPadding Horizontal padding.
     * @param verticalPadding Vertical padding.
     * @param indicatorHeight Unread indicator height.
     * @param indicatorWidth Unread indicator width.
     * @param indicatorRadius Unread indicator corner radius.
     */
    data class NotificationCardDimens(
        val radius: Dp,
        val borderWidth: Dp,
        val horizontalPadding: Dp,
        val verticalPadding: Dp,
        val indicatorHeight: Dp,
        val indicatorWidth: Dp,
        val indicatorRadius: Dp,
    )

    @Composable
    fun dimens(
        radius: Dp = 8.dp,
        borderWidth: Dp = 1.dp,
        horizontalPadding: Dp = 16.dp,
        verticalPadding: Dp = 12.dp,
        indicatorHeight: Dp = 64.dp,
        indicatorWidth: Dp = 4.dp,
        indicatorRadius: Dp = 2.dp,
    ) = NotificationCardDimens(
        radius = radius,
        borderWidth = borderWidth,
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        indicatorHeight = indicatorHeight,
        indicatorWidth = indicatorWidth,
        indicatorRadius = indicatorRadius,
    )
}

/**
 * Notification card with unread indicator.
 *
 * Shows title, timestamp, content with visual indicator for unread state.
 *
 * @param state Current UI state.
 * @param onClick Called when card is clicked.
 * @param modifier Applied to card.
 * @param colors Color configuration, defaults to [NotificationCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [NotificationCardDefaults.dimens].
 *
 * @see NotificationCardDefaults for default values
 */
@Composable
fun NotificationCard(
    state: NotificationCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: NotificationCardDefaults.NotificationCardColors = NotificationCardDefaults.colors(),
    dimens: NotificationCardDefaults.NotificationCardDimens = NotificationCardDefaults.dimens(),
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimens.radius),
        border = BorderStroke(dimens.borderWidth, colors.border),
        colors = CardDefaults.cardColors(containerColor = colors.container),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            UnreadIndicator(
                isRead = state.isRead,
                colors = colors,
                dimens = dimens,
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = dimens.verticalPadding,
                            horizontal = dimens.horizontalPadding,
                        ),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.title,
                        style = System.font.body.base.bold,
                        color = colors.title,
                        modifier = Modifier.weight(1f),
                    )

                    Text(
                        text = state.dateTime,
                        style = System.font.body.caption,
                        color = colors.dateTime,
                        textAlign = TextAlign.End,
                    )
                }

                Spacer(Modifier.height(8.dp))

                HorizontalDivider(color = colors.divider, thickness = 1.dp)

                Spacer(Modifier.height(12.dp))

                Text(
                    text = state.content,
                    style = System.font.body.caption,
                    color = colors.content,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.UnreadIndicator(
    isRead: Boolean,
    colors: NotificationCardDefaults.NotificationCardColors,
    dimens: NotificationCardDefaults.NotificationCardDimens,
) {
    Box(
        modifier =
            Modifier
                .align(Alignment.CenterStart)
                .height(dimens.indicatorHeight)
                .width(dimens.indicatorWidth)
                .clip(
                    RoundedCornerShape(
                        topEnd = dimens.indicatorRadius,
                        bottomEnd = dimens.indicatorRadius,
                    )
                ).background(if (isRead) Color.Transparent else colors.unreadIndicator),
    )
}

@Preview
@Composable
private fun NotificationCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        NotificationCard(
            state =
                NotificationCardState(
                    title = "New promotion",
                    dateTime = "10:30 AM",
                    content = "50% discount on all rides this weekend! Don't miss out.",
                    isRead = false,
                ),
            onClick = {},
        )
    }
}
