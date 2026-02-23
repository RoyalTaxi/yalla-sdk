package uz.yalla.components.composite.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.ic_coin

/**
 * Default configuration values for [BonusAmountItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object BonusAmountItemDefaults {
    /**
     * Color configuration for [BonusAmountItem].
     *
     * @param background Background brush (gradient) of the item.
     * @param text Text color.
     */
    data class BonusAmountItemColors(
        val background: Brush,
        val text: Color
    )

    @Composable
    fun colors(
        background: Brush = System.color.sunsetNight,
        text: Color = System.color.textWhite
    ) = BonusAmountItemColors(
        background = background,
        text = text
    )

    /**
     * Text style configuration for [BonusAmountItem].
     *
     * @param amount Style for the bonus amount text.
     */
    data class BonusAmountItemStyle(
        val amount: TextStyle
    )

    @Composable
    fun style(amount: TextStyle = System.font.body.base.bold) =
        BonusAmountItemStyle(
            amount = amount
        )

    /**
     * Dimension configuration for [BonusAmountItem].
     *
     * @param shape Shape of the item container.
     * @param contentPadding Padding inside the container.
     * @param iconSize Size of the coin icon.
     * @param iconSpacing Spacing between icon and text.
     * @param trailingSpacing Extra spacing at the end.
     */
    data class BonusAmountItemDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val iconSize: Dp,
        val iconSpacing: Dp,
        val trailingSpacing: Dp
    )

    @Composable
    fun dimens(
        shape: Shape = CircleShape,
        contentPadding: PaddingValues = PaddingValues(4.dp),
        iconSize: Dp = 20.dp,
        iconSpacing: Dp = 4.dp,
        trailingSpacing: Dp = 12.dp
    ) = BonusAmountItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconSpacing = iconSpacing,
        trailingSpacing = trailingSpacing
    )
}

/**
 * Clickable bonus amount display with coin icon.
 *
 * ## Usage
 *
 * ```kotlin
 * ClickableBonusAmountItem(
 *     bonus = 5000,
 *     onClick = { navigateToBonusScreen() }
 * )
 * ```
 *
 * @param bonus Bonus amount to display.
 * @param onClick Called when the item is clicked.
 * @param modifier Applied to the surface.
 * @param colors Color configuration, defaults to [BonusAmountItemDefaults.colors].
 * @param style Text style configuration, defaults to [BonusAmountItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [BonusAmountItemDefaults.dimens].
 */
@Composable
fun ClickableBonusAmountItem(
    bonus: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BonusAmountItemDefaults.BonusAmountItemColors = BonusAmountItemDefaults.colors(),
    style: BonusAmountItemDefaults.BonusAmountItemStyle = BonusAmountItemDefaults.style(),
    dimens: BonusAmountItemDefaults.BonusAmountItemDimens = BonusAmountItemDefaults.dimens()
) = Surface(
    shape = dimens.shape,
    color = Color.Transparent,
    modifier = modifier,
    onClick = onClick
) {
    BonusAmountItemContent(
        bonus = bonus,
        colors = colors,
        style = style,
        dimens = dimens
    )
}

/**
 * Non-clickable bonus amount display in a card.
 *
 * ## Usage
 *
 * ```kotlin
 * BonusAmountItemView(bonus = 5000)
 * ```
 *
 * @param bonus Bonus amount to display.
 * @param modifier Applied to the card.
 * @param colors Color configuration, defaults to [BonusAmountItemDefaults.colors].
 * @param style Text style configuration, defaults to [BonusAmountItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [BonusAmountItemDefaults.dimens].
 */
@Composable
fun BonusAmountItemView(
    bonus: Long,
    modifier: Modifier = Modifier,
    colors: BonusAmountItemDefaults.BonusAmountItemColors = BonusAmountItemDefaults.colors(),
    style: BonusAmountItemDefaults.BonusAmountItemStyle = BonusAmountItemDefaults.style(),
    dimens: BonusAmountItemDefaults.BonusAmountItemDimens = BonusAmountItemDefaults.dimens()
) = Card(
    shape = dimens.shape,
    modifier = modifier
) {
    BonusAmountItemContent(
        bonus = bonus,
        colors = colors,
        style = style,
        dimens = dimens
    )
}

@Composable
private fun BonusAmountItemContent(
    bonus: Long,
    colors: BonusAmountItemDefaults.BonusAmountItemColors,
    style: BonusAmountItemDefaults.BonusAmountItemStyle,
    dimens: BonusAmountItemDefaults.BonusAmountItemDimens
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.iconSpacing),
        modifier =
            Modifier
                .background(colors.background)
                .padding(dimens.contentPadding)
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_coin),
            contentDescription = null,
            modifier = Modifier.size(dimens.iconSize)
        )

        Text(
            text = bonus.toString(),
            style = style.amount,
            color = colors.text
        )

        Spacer(modifier = Modifier.size(dimens.trailingSpacing))
    }
}
