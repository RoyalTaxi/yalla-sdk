package uz.yalla.composites.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * UI state for [PromotionCard].
 *
 * @param title Primary promotional text.
 * @param subtitle Secondary description.
 */
data class PromotionCardState(
    val title: String,
    val subtitle: String,
)

/**
 * Promotional CTA card for special offers.
 *
 * Use for "Become a driver" or similar promotional actions.
 *
 * ## Usage
 *
 * ```kotlin
 * PromotionCard(
 *     state = PromotionCardState(
 *         title = "Become a Driver",
 *         subtitle = "Start earning today",
 *     ),
 *     onClick = { navigateToDriverSignup() },
 * )
 * ```
 *
 * @param state Current UI state.
 * @param onClick Called when card is clicked.
 * @param modifier Applied to card.
 * @param trailingContent Optional right-side content (icon/image).
 * @param colors Color configuration, defaults to [PromotionCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [PromotionCardDefaults.dimens].
 *
 * @see PromotionCardDefaults for default values
 */
@Composable
fun PromotionCard(
    state: PromotionCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: PromotionCardDefaults.PromotionCardColors = PromotionCardDefaults.colors(),
    dimens: PromotionCardDefaults.PromotionCardDimens = PromotionCardDefaults.dimens(),
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(dimens.height),
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
            ),
    ) {
        Row(
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = state.title,
                    style = System.font.title.base,
                    color = colors.title,
                )

                Spacer(Modifier.height(dimens.titleSubtitleSpacing))

                Text(
                    text = state.subtitle,
                    style = System.font.body.small.medium,
                    color = colors.subtitle,
                )
            }

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

/**
 * Default values for [PromotionCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object PromotionCardDefaults {
    /**
     * Color configuration for [PromotionCard].
     *
     * @param container Background color.
     * @param title Title text color.
     * @param subtitle Subtitle text color.
     */
    data class PromotionCardColors(
        val container: Color,
        val title: Color,
        val subtitle: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        title: Color = System.color.textBase,
        subtitle: Color = System.color.textSubtle,
    ): PromotionCardColors =
        PromotionCardColors(
            container = container,
            title = title,
            subtitle = subtitle,
        )

    /**
     * Dimension configuration for [PromotionCard].
     *
     * @param shape Card corner shape.
     * @param height Card height.
     * @param contentPadding Padding around content.
     * @param titleSubtitleSpacing Spacing between title and subtitle.
     */
    data class PromotionCardDimens(
        val shape: Shape,
        val height: Dp,
        val contentPadding: PaddingValues,
        val titleSubtitleSpacing: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        height: Dp = 100.dp,
        contentPadding: PaddingValues = PaddingValues(16.dp),
        titleSubtitleSpacing: Dp = 4.dp,
    ): PromotionCardDimens =
        PromotionCardDimens(
            shape = shape,
            height = height,
            contentPadding = contentPadding,
            titleSubtitleSpacing = titleSubtitleSpacing,
        )
}

@Preview
@Composable
private fun PromotionCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        PromotionCard(
            state =
                PromotionCardState(
                    title = "Become a Driver",
                    subtitle = "Start earning today",
                ),
            onClick = {},
        )
    }
}
