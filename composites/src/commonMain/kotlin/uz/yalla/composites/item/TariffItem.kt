package uz.yalla.composites.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * UI state for [TariffItem].
 *
 * @param name Tariff name.
 * @param price Formatted price.
 * @param selected Whether tariff is selected.
 */
data class TariffItemState(
    val name: String,
    val price: String,
    val selected: Boolean,
)

/**
 * Tariff selection card with name, price, and optional image.
 *
 * Shows selected state with gradient border.
 *
 * ## Usage
 *
 * ```kotlin
 * TariffItem(
 *     state = TariffItemState(
 *         name = "Comfort",
 *         price = "25,000 sum",
 *         selected = selectedTariff == tariff,
 *     ),
 *     onClick = { selectTariff(tariff) },
 *     image = {
 *         AsyncImage(
 *             model = tariff.iconUrl,
 *             contentDescription = null,
 *         )
 *     },
 * )
 * ```
 *
 * @param state Current UI state.
 * @param onClick Called when item is clicked.
 * @param modifier Applied to item.
 * @param image Optional bottom image slot.
 * @param colors Color configuration, defaults to [TariffItemDefaults.colors].
 * @param style Text style configuration, defaults to [TariffItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [TariffItemDefaults.dimens].
 *
 * @see TariffItemDefaults for default values
 */
@Composable
fun TariffItem(
    state: TariffItemState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    colors: TariffItemDefaults.TariffItemColors = TariffItemDefaults.colors(),
    style: TariffItemDefaults.TariffItemStyle = TariffItemDefaults.style(),
    dimens: TariffItemDefaults.TariffItemDimens = TariffItemDefaults.dimens(),
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .height(dimens.height)
                .widthIn(min = dimens.minWidth),
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (state.selected) {
                        colors.selectedContainer
                    } else {
                        colors.container
                    },
            ),
        border =
            if (state.selected) {
                BorderStroke(
                    width = dimens.selectedBorderWidth,
                    brush = colors.selectedBorder,
                )
            } else {
                null
            },
    ) {
        Column(modifier = Modifier.padding(dimens.contentPadding)) {
            Text(
                text = state.name,
                color = colors.name,
                style = style.name,
                maxLines = dimens.textMaxLines,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(dimens.namePriceSpacing))

            Text(
                text = state.price,
                color = colors.price,
                style = style.price,
                maxLines = dimens.textMaxLines,
                overflow = TextOverflow.Ellipsis,
            )

            if (image != null) {
                Spacer(Modifier.height(dimens.priceImageSpacing))
                image()
            }
        }
    }
}

/**
 * Default configuration values for [TariffItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object TariffItemDefaults {
    /**
     * Color configuration for [TariffItem].
     *
     * @param container Background color when not selected.
     * @param selectedContainer Background color when selected.
     * @param name Name text color.
     * @param price Price text color.
     * @param selectedBorder Border brush when selected.
     */
    data class TariffItemColors(
        val container: Color,
        val selectedContainer: Color,
        val name: Color,
        val price: Color,
        val selectedBorder: Brush,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        selectedContainer: Color = System.color.backgroundBase,
        name: Color = System.color.textBase,
        price: Color = System.color.textBase,
        selectedBorder: Brush = System.color.sunsetNight,
    ) = TariffItemColors(
        container = container,
        selectedContainer = selectedContainer,
        name = name,
        price = price,
        selectedBorder = selectedBorder,
    )

    /**
     * Text style configuration for [TariffItem].
     *
     * @param name Name text style.
     * @param price Price text style.
     */
    data class TariffItemStyle(
        val name: TextStyle,
        val price: TextStyle,
    )

    @Composable
    fun style(
        name: TextStyle = System.font.body.base.bold,
        price: TextStyle = System.font.body.base.bold,
    ) = TariffItemStyle(
        name = name,
        price = price,
    )

    /**
     * Dimension configuration for [TariffItem].
     *
     * @param shape Item corner shape.
     * @param height Item height.
     * @param minWidth Minimum item width.
     * @param contentPadding Padding around content.
     * @param selectedBorderWidth Border width when selected.
     * @param namePriceSpacing Spacing between name and price.
     * @param priceImageSpacing Spacing between price and image.
     * @param textMaxLines Maximum lines for name and price text.
     */
    data class TariffItemDimens(
        val shape: Shape,
        val height: Dp,
        val minWidth: Dp,
        val contentPadding: Dp,
        val selectedBorderWidth: Dp,
        val namePriceSpacing: Dp,
        val priceImageSpacing: Dp,
        val textMaxLines: Int,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        height: Dp = 120.dp,
        minWidth: Dp = 140.dp,
        contentPadding: Dp = 12.dp,
        selectedBorderWidth: Dp = 2.dp,
        namePriceSpacing: Dp = 6.dp,
        priceImageSpacing: Dp = 10.dp,
        textMaxLines: Int = 1,
    ) = TariffItemDimens(
        shape = shape,
        height = height,
        minWidth = minWidth,
        contentPadding = contentPadding,
        selectedBorderWidth = selectedBorderWidth,
        namePriceSpacing = namePriceSpacing,
        priceImageSpacing = priceImageSpacing,
        textMaxLines = textMaxLines,
    )
}

@Preview
@Composable
private fun TariffItemPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        TariffItem(
            state =
                TariffItemState(
                    name = "Comfort",
                    price = "25,000 sum",
                    selected = true,
                ),
            onClick = {},
        )
    }
}
