package uz.yalla.composites.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

/**
 * Status for [HistoryCard].
 */
enum class HistoryCardStatus(
    val label: String
) {
    Completed("Completed"),
    Canceled("Canceled"),
}

/**
 * UI state for [HistoryCard].
 *
 * @param time Display time.
 * @param price Formatted price.
 * @param status Order status.
 */
data class HistoryCardState(
    val time: String,
    val price: String,
    val status: HistoryCardStatus,
)

/**
 * Default configuration for [HistoryCard].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object HistoryCardDefaults {
    /**
     * Color configuration for [HistoryCard].
     *
     * @param container Background color.
     * @param time Time text color.
     * @param price Price text color.
     * @param statusCompleted Completed status text color.
     * @param statusCanceled Canceled status text color.
     */
    data class HistoryCardColors(
        val container: Color,
        val time: Color,
        val price: Color,
        val statusCompleted: Color,
        val statusCanceled: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        time: Color = System.color.textBase,
        price: Color = System.color.textBase,
        statusCompleted: Color = System.color.textLink,
        statusCanceled: Color = System.color.textRed,
    ) = HistoryCardColors(
        container = container,
        time = time,
        price = price,
        statusCompleted = statusCompleted,
        statusCanceled = statusCanceled,
    )

    /**
     * Text style configuration for [HistoryCard].
     *
     * @param time Time text style.
     * @param status Status text style.
     * @param price Price text style.
     */
    data class HistoryCardStyle(
        val time: TextStyle,
        val status: TextStyle,
        val price: TextStyle,
    )

    @Composable
    fun style(
        time: TextStyle = System.font.body.caption,
        status: TextStyle = System.font.body.caption,
        price: TextStyle = System.font.body.base.bold,
    ) = HistoryCardStyle(
        time = time,
        status = status,
        price = price,
    )

    /**
     * Dimension configuration for [HistoryCard].
     *
     * @param shape Card corner shape.
     * @param contentPadding Padding around content.
     * @param spacingSmall Small spacing between elements.
     * @param spacingMedium Medium spacing between sections.
     */
    data class HistoryCardDimens(
        val shape: Shape,
        val contentPadding: Dp,
        val spacingSmall: Dp,
        val spacingMedium: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: Dp = 16.dp,
        spacingSmall: Dp = 8.dp,
        spacingMedium: Dp = 16.dp,
    ) = HistoryCardDimens(
        shape = shape,
        contentPadding = contentPadding,
        spacingSmall = spacingSmall,
        spacingMedium = spacingMedium,
    )
}

/**
 * History item card for ride/order history.
 *
 * Displays route, time, status, price, and optional image.
 *
 * @param state Current UI state.
 * @param onClick Called when card is clicked.
 * @param modifier Applied to card.
 * @param route Composable route display.
 * @param image Optional right-side image.
 * @param colors Color configuration, defaults to [HistoryCardDefaults.colors].
 * @param style Text style configuration, defaults to [HistoryCardDefaults.style].
 * @param dimens Dimension configuration, defaults to [HistoryCardDefaults.dimens].
 *
 * @see HistoryCardDefaults for default values
 */
@Composable
fun HistoryCard(
    state: HistoryCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    route: (@Composable () -> Unit)? = null,
    image: (@Composable () -> Unit)? = null,
    colors: HistoryCardDefaults.HistoryCardColors = HistoryCardDefaults.colors(),
    style: HistoryCardDefaults.HistoryCardStyle = HistoryCardDefaults.style(),
    dimens: HistoryCardDefaults.HistoryCardDimens = HistoryCardDefaults.dimens(),
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = dimens.shape,
        colors = CardDefaults.cardColors(containerColor = colors.container),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(dimens.contentPadding),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .weight(1f),
            ) {
                route?.let {
                    it()
                    Spacer(Modifier.height(dimens.spacingSmall))
                }
                Spacer(Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.time,
                        color = colors.time,
                        style = style.time,
                    )

                    Spacer(Modifier.width(dimens.spacingSmall))

                    Text(
                        text = state.status.label,
                        color =
                            when (state.status) {
                                HistoryCardStatus.Completed -> colors.statusCompleted
                                HistoryCardStatus.Canceled -> colors.statusCanceled
                            },
                        style = style.status,
                    )
                }
            }

            Spacer(Modifier.width(dimens.spacingMedium))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = state.price,
                    color = colors.price,
                    style = style.price,
                )

                image?.invoke()
            }
        }
    }
}

@Preview
@Composable
private fun HistoryCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        HistoryCard(
            state =
                HistoryCardState(
                    time = "10:30 AM",
                    price = "25,000 sum",
                    status = HistoryCardStatus.Completed,
                ),
            onClick = {},
        )
    }
}
