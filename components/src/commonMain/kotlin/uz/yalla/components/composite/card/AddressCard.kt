package uz.yalla.components.composite.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * UI state for [AddressCard].
 *
 * @param name Place name (Home, Work, custom).
 * @param address Street address.
 * @param metadata Optional footer text (e.g., duration).
 */
data class AddressCardState(
    val name: String,
    val address: String,
    val metadata: String? = null,
)

/**
 * Compact address card with name, address, and optional metadata.
 *
 * Use for horizontal scrolling address lists.
 *
 * ## Usage
 *
 * ```kotlin
 * AddressCard(
 *     state = AddressCardState(
 *         name = "Home",
 *         address = "123 Main Street",
 *         metadata = "5 min",
 *     ),
 *     icon = painterResource(Res.drawable.ic_circle_point),
 *     onClick = { selectAddress(address) },
 * )
 * ```
 *
 * @param state Current UI state.
 * @param onClick Called when card is clicked.
 * @param modifier Applied to card.
 * @param icon Optional leading icon.
 * @param colors Color configuration, defaults to [AddressCardDefaults.colors].
 * @param dimens Dimension configuration, defaults to [AddressCardDefaults.dimens].
 *
 * @see AddressCardDefaults for default values
 */
@Composable
fun AddressCard(
    state: AddressCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    colors: AddressCardDefaults.AddressCardColors = AddressCardDefaults.colors(),
    dimens: AddressCardDefaults.AddressCardDimens = AddressCardDefaults.dimens(),
) {
    Card(
        onClick = onClick,
        modifier =
            modifier
                .widthIn(max = dimens.maxWidth)
                .height(dimens.height),
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
            ),
    ) {
        Column(
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                    Spacer(Modifier.width(dimens.iconSpacing))
                }

                Text(
                    text = state.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.name,
                    style = System.font.body.base.bold,
                )
            }

            Spacer(Modifier.height(dimens.contentSpacing))

            Text(
                text = state.address,
                color = colors.address,
                style = System.font.body.small.medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.weight(1f))

            if (state.metadata != null) {
                Text(
                    text = state.metadata,
                    color = colors.metadata,
                    style = System.font.body.base.bold,
                )
            }
        }
    }
}

/**
 * Default values for [AddressCard].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 */
object AddressCardDefaults {
    /**
     * Color configuration for [AddressCard].
     *
     * @param container Background color.
     * @param name Name text color.
     * @param address Address text color.
     * @param metadata Metadata text color.
     */
    data class AddressCardColors(
        val container: Color,
        val name: Color,
        val address: Color,
        val metadata: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        name: Color = System.color.textBase,
        address: Color = System.color.textBase,
        metadata: Color = System.color.textBase,
    ): AddressCardColors =
        AddressCardColors(
            container = container,
            name = name,
            address = address,
            metadata = metadata,
        )

    /**
     * Dimension configuration for [AddressCard].
     *
     * @param shape Card corner shape.
     * @param maxWidth Maximum card width.
     * @param height Card height.
     * @param iconSpacing Spacing after icon.
     * @param contentSpacing Spacing between content elements.
     * @param contentPadding Padding around content.
     */
    data class AddressCardDimens(
        val shape: Shape,
        val maxWidth: Dp,
        val height: Dp,
        val iconSpacing: Dp,
        val contentSpacing: Dp,
        val contentPadding: PaddingValues,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(20.dp),
        maxWidth: Dp = 248.dp,
        height: Dp = 120.dp,
        iconSpacing: Dp = 8.dp,
        contentSpacing: Dp = 8.dp,
        contentPadding: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = 20.dp),
    ): AddressCardDimens =
        AddressCardDimens(
            shape = shape,
            maxWidth = maxWidth,
            height = height,
            iconSpacing = iconSpacing,
            contentSpacing = contentSpacing,
            contentPadding = contentPadding,
        )
}

@Preview
@Composable
private fun AddressCardPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        AddressCard(
            state =
                AddressCardState(
                    name = "Home",
                    address = "123 Main Street",
                    metadata = "5 min",
                ),
            onClick = {},
        )
    }
}
