package uz.yalla.components.composite.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.platform.toggle.NativeSwitch

/**
 * UI state for [ServiceItem].
 *
 * @param name Service name.
 * @param price Formatted price text.
 * @param checked Whether service is selected.
 */
data class ServiceItemState(
    val name: String,
    val price: String,
    val checked: Boolean,
)

/**
 * Service toggle item with name, price, and switch.
 *
 * Use for optional service selection in order flow.
 *
 * ## Usage
 *
 * ```kotlin
 * ServiceItem(
 *     state = ServiceItemState(
 *         name = "Child seat",
 *         price = "+5,000 sum",
 *         checked = hasChildSeat,
 *     ),
 *     onCheckedChange = { viewModel.toggleChildSeat(it) },
 * )
 * ```
 *
 * @param state Current UI state.
 * @param onCheckedChange Called when switch changes.
 * @param modifier Applied to item.
 * @param enabled Whether item is enabled.
 * @param colors Color configuration, defaults to [ServiceItemDefaults.colors].
 * @param style Text style configuration, defaults to [ServiceItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [ServiceItemDefaults.dimens].
 *
 * @see ServiceItemDefaults for default values
 */
@Composable
fun ServiceItem(
    state: ServiceItemState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ServiceItemDefaults.ServiceItemColors = ServiceItemDefaults.colors(),
    style: ServiceItemDefaults.ServiceItemStyle = ServiceItemDefaults.style(),
    dimens: ServiceItemDefaults.ServiceItemDimens = ServiceItemDefaults.dimens(),
) {
    Card(
        onClick = { onCheckedChange(!state.checked) },
        modifier = modifier,
        enabled = enabled,
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
                disabledContainerColor = colors.container,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(dimens.contentPadding),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.titlePriceSpacing),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = state.name,
                    color = colors.name,
                    style = style.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

                Text(
                    text = state.price,
                    color = colors.price,
                    style = style.price,
                )
            }

            NativeSwitch(
                checked = state.checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        }
    }
}

/**
 * Default configuration values for [ServiceItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object ServiceItemDefaults {
    /**
     * Color configuration for [ServiceItem].
     *
     * @param container Background color.
     * @param name Name text color.
     * @param price Price text color.
     */
    data class ServiceItemColors(
        val container: Color,
        val name: Color,
        val price: Color,
    )

    @Composable
    fun colors(
        container: Color = Color.Transparent,
        name: Color = System.color.textBase,
        price: Color = System.color.textSubtle,
    ) = ServiceItemColors(
        container = container,
        name = name,
        price = price,
    )

    /**
     * Text style configuration for [ServiceItem].
     *
     * @param name Name text style.
     * @param price Price text style.
     */
    data class ServiceItemStyle(
        val name: TextStyle,
        val price: TextStyle,
    )

    @Composable
    fun style(
        name: TextStyle = System.font.body.base.bold,
        price: TextStyle = System.font.body.small.medium,
    ) = ServiceItemStyle(
        name = name,
        price = price,
    )

    /**
     * Dimension configuration for [ServiceItem].
     *
     * @param shape Item shape.
     * @param contentPadding Padding around content.
     * @param contentSpacing Spacing between elements.
     * @param titlePriceSpacing Spacing between title and price.
     */
    data class ServiceItemDimens(
        val shape: Shape,
        val contentPadding: PaddingValues,
        val contentSpacing: Dp,
        val titlePriceSpacing: Dp,
    )

    @Composable
    fun dimens(
        shape: Shape = RectangleShape,
        contentPadding: PaddingValues =
            PaddingValues(
                vertical = 8.dp,
                horizontal = 20.dp,
            ),
        contentSpacing: Dp = 16.dp,
        titlePriceSpacing: Dp = 8.dp,
    ) = ServiceItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        titlePriceSpacing = titlePriceSpacing,
    )
}

@Preview
@Composable
private fun ServiceItemPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        ServiceItem(
            state =
                ServiceItemState(
                    name = "Child seat",
                    price = "+5,000 sum",
                    checked = true,
                ),
            onCheckedChange = {},
        )
    }
}
