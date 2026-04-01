package uz.yalla.composites.item

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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import uz.yalla.design.theme.System
import uz.yalla.resources.Res
import uz.yalla.resources.img_coin

/**
 * Color configuration for [ClickableValueItem] and [ValueItemView].
 *
 * @param background Gradient brush for the item background.
 * @param text Text color for the bonus value.
 * @since 0.0.1
 */
@Immutable
data class ValueItemColors(
    val background: Brush,
    val text: Color,
)

/**
 * Dimension configuration for [ClickableValueItem] and [ValueItemView].
 *
 * @param shape Item shape (pill by default).
 * @param contentPadding Padding inside the item.
 * @param iconSize Size of the coin icon.
 * @param iconSpacing Spacing between coin icon and text.
 * @param trailingSpacing Trailing spacer width after the text.
 * @since 0.0.1
 */
@Immutable
data class ValueItemDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val iconSpacing: Dp,
    val trailingSpacing: Dp,
)

/**
 * Default configuration values for [ClickableValueItem] and [ValueItemView].
 *
 * @since 0.0.1
 */
object ValueItemDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        background: Brush = System.color.gradient.sunsetNight,
        text: Color = System.color.text.white,
    ) = ValueItemColors(
        background = background,
        text = text,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = CircleShape,
        contentPadding: PaddingValues = PaddingValues(4.dp),
        iconSize: Dp = 20.dp,
        iconSpacing: Dp = 4.dp,
        trailingSpacing: Dp = 12.dp,
    ) = ValueItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSize = iconSize,
        iconSpacing = iconSpacing,
        trailingSpacing = trailingSpacing,
    )
}

/**
 * Clickable bonus value pill displaying a coin icon and numeric balance.
 *
 * Wrapped in a [Surface][androidx.compose.material3.Surface] for click handling.
 * Use in app bars or headers where the user can tap to view bonus details.
 *
 * ## Usage
 *
 * ```kotlin
 * ClickableValueItem(
 *     bonus = user.bonusBalance,
 *     onClick = { navigateToBonuses() },
 * )
 * ```
 *
 * @param bonus Numeric bonus value to display.
 * @param onClick Called when the item is clicked.
 * @param modifier Applied to the root surface.
 * @param colors Color configuration, defaults to [ValueItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ValueItemDefaults.dimens].
 *
 * @see ValueItemView
 * @see ValueItemDefaults
 * @since 0.0.1
 */
@Composable
fun ClickableValueItem(
    bonus: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ValueItemColors = ValueItemDefaults.colors(),
    dimens: ValueItemDimens = ValueItemDefaults.dimens(),
) = Surface(
    shape = dimens.shape,
    color = Color.Transparent,
    modifier = modifier,
    onClick = onClick,
) {
    ValueItemContent(
        bonus = bonus,
        colors = colors,
        dimens = dimens,
    )
}

/**
 * Non-interactive bonus value pill displaying a coin icon and numeric balance.
 *
 * Use for read-only display of bonus balance in cards and lists.
 *
 * ## Usage
 *
 * ```kotlin
 * ValueItemView(bonus = order.bonusEarned)
 * ```
 *
 * @param bonus Numeric bonus value to display.
 * @param modifier Applied to the root card.
 * @param colors Color configuration, defaults to [ValueItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [ValueItemDefaults.dimens].
 *
 * @see ClickableValueItem
 * @see ValueItemDefaults
 * @since 0.0.1
 */
@Composable
fun ValueItemView(
    bonus: Long,
    modifier: Modifier = Modifier,
    colors: ValueItemColors = ValueItemDefaults.colors(),
    dimens: ValueItemDimens = ValueItemDefaults.dimens(),
) = Card(
    shape = dimens.shape,
    modifier = modifier,
) {
    ValueItemContent(
        bonus = bonus,
        colors = colors,
        dimens = dimens,
    )
}

@Composable
private fun ValueItemContent(
    bonus: Long,
    colors: ValueItemColors,
    dimens: ValueItemDimens,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.iconSpacing),
        modifier =
            Modifier
                .background(colors.background)
                .padding(dimens.contentPadding),
    ) {
        Image(
            painter = painterResource(Res.drawable.img_coin),
            contentDescription = null,
            modifier = Modifier.size(dimens.iconSize),
        )

        Text(
            text = bonus.toString(),
            style = System.font.body.base.bold,
            color = colors.text,
        )

        Spacer(modifier = Modifier.size(dimens.trailingSpacing))
    }
}
