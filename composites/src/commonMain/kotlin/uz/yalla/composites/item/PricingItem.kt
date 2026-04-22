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
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [PricingItem].
 *
 * @param container Background color when not selected.
 * @param selectedContainer Background color when selected.
 * @param name Text color for the service name.
 * @param price Text color for the price.
 * @param selectedBorder Gradient brush for the border when selected.
 * @since 0.0.1
 */
@Immutable
data class PricingItemColors(
    val container: Color,
    val selectedContainer: Color,
    val name: Color,
    val price: Color,
    val selectedBorder: Brush,
)

/**
 * Dimension configuration for [PricingItem].
 *
 * @param shape Card shape.
 * @param height Fixed card height.
 * @param minWidth Minimum card width.
 * @param contentPadding Padding inside the card.
 * @param selectedBorderWidth Border width when selected.
 * @param namePriceSpacing Spacing between name and price texts.
 * @param priceImageSpacing Spacing between price text and image.
 * @param textMaxLines Maximum lines for name and price texts.
 * @since 0.0.1
 */
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

/**
 * Default configuration values for [PricingItem].
 *
 * @since 0.0.1
 */
object PricingItemDefaults {

    /**
     * Creates theme-aware default colors.
     */
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

    /**
     * Creates default dimensions.
     */
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

/**
 * Pricing card for ride service selection.
 *
 * Displays a service name, price, and optional vehicle image. When [selected], shows
 * a gradient border and a different background color.
 *
 * The default text style and color for [name] and [price] are injected via
 * [ProvideTextStyle][androidx.compose.material3.ProvideTextStyle]; a plain
 * [Text][androidx.compose.material3.Text] in the slot inherits them automatically.
 *
 * ## Usage
 *
 * ```kotlin
 * PricingItem(
 *     name = { Text("Standard") },
 *     price = { Text("25,000 sum") },
 *     selected = selectedService == "standard",
 *     onClick = { selectService("standard") },
 *     image = { Image(painterResource(Res.drawable.img_sedan), null) },
 * )
 * ```
 *
 * @param name Service name slot (e.g., "Standard", "Comfort"); receives [System.font.body.base.bold] style.
 * @param price Formatted price slot; receives [System.font.body.base.bold] style.
 * @param selected Whether this item is currently selected.
 * @param onClick Called when the item is clicked.
 * @param modifier Applied to the root card.
 * @param image Optional vehicle image rendered below the price.
 * @param colors Color configuration, defaults to [PricingItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [PricingItemDefaults.dimens].
 *
 * @see PricingItemDefaults
 * @since 0.0.1
 */
@Composable
fun PricingItem(
    name: @Composable () -> Unit,
    price: @Composable () -> Unit,
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
            ProvideTextStyle(
                System.font.body.base.bold
                    .copy(color = colors.name)
            ) {
                name()
            }

            Spacer(Modifier.height(dimens.namePriceSpacing))

            ProvideTextStyle(
                System.font.body.base.bold
                    .copy(color = colors.price)
            ) {
                price()
            }

            if (image != null) {
                Spacer(Modifier.height(dimens.priceImageSpacing))
                image()
            }
        }
    }
}
