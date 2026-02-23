package uz.yalla.components.composite.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * UI state for [BonusCard].
 *
 * @param title Card title.
 * @param subtitle Descriptive subtitle.
 * @param balance Formatted balance amount.
 */
data class BonusCardState(
    val title: String,
    val subtitle: String,
    val balance: String,
)

/**
 * Promotional bonus card with background image.
 *
 * Displays bonus balance with title and subtitle over styled background.
 *
 * ## Usage
 *
 * ```kotlin
 * BonusCard(
 *     state = BonusCardState(
 *         title = "Your Bonuses",
 *         subtitle = "1 ride = 5% cashback",
 *         balance = "50,000",
 *     ),
 *     background = painterResource(Res.drawable.ic_bonuses_background),
 * )
 * ```
 *
 * @param state Current UI state.
 * @param background Background image painter.
 * @param modifier Applied to card.
 * @param colors Color configuration, defaults to [BonusCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [BonusCardDefaults.dimens].
 *
 * @see BonusCardDefaults for default values
 */
@Composable
fun BonusCard(
    state: BonusCardState,
    background: Painter,
    modifier: Modifier = Modifier,
    colors: BonusCardDefaults.BonusCardColors = BonusCardDefaults.colors(),
    dimens: BonusCardDefaults.BonusCardDimens = BonusCardDefaults.dimens(),
) {
    Card(
        shape = dimens.shape,
        modifier =
            modifier
                .fillMaxWidth()
                .height(dimens.height),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .paint(
                        painter = background,
                        contentScale = ContentScale.Crop,
                    ).padding(dimens.contentPadding),
        ) {
            Text(
                text = state.title,
                style = System.font.body.base.medium,
                color = colors.title,
            )

            Text(
                text = state.subtitle,
                style = System.font.body.small.medium,
                color = colors.subtitle,
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = state.balance,
                style = System.font.title.xLarge,
                color = colors.balance,
            )
        }
    }
}

/**
 * Default values for [BonusCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object BonusCardDefaults {
    /**
     * Color configuration for [BonusCard].
     *
     * @param title Title text color.
     * @param subtitle Subtitle text color.
     * @param balance Balance text color.
     */
    data class BonusCardColors(
        val title: Color,
        val subtitle: Color,
        val balance: Color,
    )

    @Composable
    fun colors(
        title: Color = System.color.textWhite,
        subtitle: Color = System.color.textWhite,
        balance: Color = System.color.textWhite,
    ): BonusCardColors =
        BonusCardColors(
            title = title,
            subtitle = subtitle,
            balance = balance,
        )

    /**
     * Dimension configuration for [BonusCard].
     *
     * @param shape Card corner shape.
     * @param height Card height.
     * @param contentPadding Padding around content.
     */
    data class BonusCardDimens(
        val shape: Shape,
        val height: Dp,
        val contentPadding: PaddingValues,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        height: Dp = 148.dp,
        contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 20.dp),
    ): BonusCardDimens =
        BonusCardDimens(
            shape = shape,
            height = height,
            contentPadding = contentPadding,
        )
}

@Preview
@Composable
private fun BonusCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.Gray)
                .padding(16.dp)
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .background(Color.Blue.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
        ) {
            Text(
                text = "Your Bonuses",
                style = System.font.body.base.medium,
                color = Color.White,
            )
            Text(
                text = "1 ride = 5% cashback",
                style = System.font.body.small.medium,
                color = Color.White,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "50,000",
                style = System.font.title.xLarge,
                color = Color.White,
            )
        }
    }
}
