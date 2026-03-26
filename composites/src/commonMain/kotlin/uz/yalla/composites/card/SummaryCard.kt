package uz.yalla.composites.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * Color configuration for [SummaryCard].
 *
 * @param container Card background color.
 * @since 0.0.5-alpha11
 */
@Immutable
data class SummaryCardColors(
    val container: Color,
)

/**
 * Dimension configuration for [SummaryCard].
 *
 * @param shape Card corner shape.
 * @param contentPadding Padding inside the card.
 * @param headerSpacing Spacing between header and content.
 * @param trailingSpacing Spacing between main column and trailing column.
 * @since 0.0.5-alpha11
 */
@Immutable
data class SummaryCardDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val headerSpacing: Dp,
    val trailingSpacing: Dp,
)

/**
 * Default configuration values for [SummaryCard].
 *
 * @since 0.0.5-alpha11
 */
object SummaryCardDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
    ): SummaryCardColors = SummaryCardColors(
        container = container,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(16.dp),
        headerSpacing: Dp = 8.dp,
        trailingSpacing: Dp = 16.dp,
    ): SummaryCardDimens = SummaryCardDimens(
        shape = shape,
        contentPadding = contentPadding,
        headerSpacing = headerSpacing,
        trailingSpacing = trailingSpacing,
    )
}

/**
 * Two-column summary card with header, content, and trailing slots.
 *
 * Built on [ContentCard]. Layout: left column (header → spacer → content)
 * paired with right column (trailing content aligned to end).
 *
 * ## Usage
 *
 * ```kotlin
 * SummaryCard(
 *     onClick = { openDetails() },
 *     header = { RouteView(from = origin, to = destination) },
 *     trailing = {
 *         Text("25,000 sum", style = System.font.body.base.bold)
 *         Image(painterResource(Res.drawable.img_car), null)
 *     },
 * ) {
 *     Row {
 *         Text("10:30 AM", style = System.font.body.caption)
 *         Spacer(Modifier.width(8.dp))
 *         Text("Completed", color = System.color.text.link)
 *     }
 * }
 * ```
 *
 * @param onClick Called when the card is clicked.
 * @param modifier Applied to the root card.
 * @param colors Color configuration, defaults to [SummaryCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [SummaryCardDefaults.dimens].
 * @param header Optional top content in the left column (e.g., route display).
 * @param trailing Optional right column content (e.g., price, image).
 * @param content Bottom content in the left column (e.g., time + status row).
 *
 * @see ContentCard
 * @see SummaryCardDefaults
 * @since 0.0.5-alpha11
 */
@Composable
fun SummaryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SummaryCardColors = SummaryCardDefaults.colors(),
    dimens: SummaryCardDimens = SummaryCardDefaults.dimens(),
    header: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ContentCard(
        modifier = modifier,
        onClick = onClick,
        colors = ContentCardDefaults.colors(container = colors.container),
        dimens = ContentCardDefaults.dimens(
            shape = dimens.shape,
            contentPadding = dimens.contentPadding,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
            ) {
                if (header != null) {
                    header()
                    Spacer(Modifier.height(dimens.headerSpacing))
                }
                Spacer(Modifier.weight(1f))
                content()
            }

            if (trailing != null) {
                Spacer(Modifier.width(dimens.trailingSpacing))
                Column(horizontalAlignment = Alignment.End) {
                    trailing()
                }
            }
        }
    }
}
