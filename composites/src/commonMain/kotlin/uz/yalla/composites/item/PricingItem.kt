package uz.yalla.composites.item

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Immutable
data class PricingItemColors(
    val container: Color,
    val selectedContainer: Color,
    val name: Color,
    val price: Color,
    val selectedBorder: Brush,
)

@Immutable
data class PricingItemDimens(
    val shape: Shape,
    val height: Dp,
    val minWidth: Dp,
    val contentPadding: Dp,
    val selectedBorderWidth: Dp,
    val namePriceSpacing: Dp,
    val priceImageSpacing: Dp,
    val textMaxLines: Int,
)

object PricingItemDefaults {

    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        selectedContainer: Color = System.color.background.base,
        name: Color = System.color.text.base,
        price: Color = System.color.text.base,
        selectedBorder: Brush = System.color.gradient.sunsetNight,
    ) = PricingItemColors(
        container = container,
        selectedContainer = selectedContainer,
        name = name,
        price = price,
        selectedBorder = selectedBorder,
    )

    fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        height: Dp = 120.dp,
        minWidth: Dp = 140.dp,
        contentPadding: Dp = 12.dp,
        selectedBorderWidth: Dp = 2.dp,
        namePriceSpacing: Dp = 6.dp,
        priceImageSpacing: Dp = 10.dp,
        textMaxLines: Int = 1,
    ) = PricingItemDimens(
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

@Composable
fun PricingItem(
    name: String,
    price: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    colors: PricingItemColors = PricingItemDefaults.colors(),
    dimens: PricingItemDimens = PricingItemDefaults.dimens(),
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
                    if (selected) {
                        colors.selectedContainer
                    } else {
                        colors.container
                    },
            ),
        border =
            if (selected) {
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
                text = name,
                color = colors.name,
                style = System.font.body.base.bold,
                maxLines = dimens.textMaxLines,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(dimens.namePriceSpacing))

            Text(
                text = price,
                color = colors.price,
                style = System.font.body.base.bold,
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
