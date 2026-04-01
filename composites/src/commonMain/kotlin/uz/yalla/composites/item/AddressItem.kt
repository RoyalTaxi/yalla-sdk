package uz.yalla.composites.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Color configuration for [AddressItem].
 *
 * @param container Button/card background color.
 * @param placeholder Text color when no locations are provided.
 * @param location Text color for location names.
 * @param arrow Arrow icon tint between locations.
 * @since 0.0.1
 */
@Immutable
data class AddressItemColors(
    val container: Color,
    val placeholder: Color,
    val location: Color,
    val arrow: Color,
)

/**
 * Dimension configuration for [AddressItem].
 *
 * @param shape Button/card shape.
 * @param minHeight Minimum item height.
 * @param contentSpacing Spacing between leading content, text, and trailing content.
 * @param flowRowSpacing Spacing between locations in the multi-location variant.
 * @param dotSize Diameter of the [AddressDot] circle.
 * @param dotBorderWidth Border width of the [AddressDot] circle.
 * @param horizontalPadding Horizontal padding for the item content.
 * @param locationMaxLines Maximum lines for each location text.
 * @since 0.0.1
 */
@Immutable
data class AddressItemDimens(
    val shape: Shape,
    val minHeight: Dp,
    val contentSpacing: Dp,
    val flowRowSpacing: Dp,
    val dotSize: Dp,
    val dotBorderWidth: Dp,
    val horizontalPadding: Dp,
    val locationMaxLines: Int,
)

/**
 * Default configuration values for [AddressItem] and [AddressDot].
 *
 * @since 0.0.1
 */
object AddressItemDefaults {

    /**
     * Creates theme-aware default colors.
     */
    @Composable
    fun colors(
        container: Color = System.color.background.secondary,
        placeholder: Color = System.color.text.subtle,
        location: Color = System.color.text.base,
        arrow: Color = System.color.icon.subtle,
    ) = AddressItemColors(
        container = container,
        placeholder = placeholder,
        location = location,
        arrow = arrow,
    )

    /**
     * Creates default dimensions.
     */
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        minHeight: Dp = 60.dp,
        contentSpacing: Dp = 12.dp,
        flowRowSpacing: Dp = 6.dp,
        dotSize: Dp = 14.dp,
        dotBorderWidth: Dp = 4.dp,
        horizontalPadding: Dp = 16.dp,
        locationMaxLines: Int = 1,
    ) = AddressItemDimens(
        shape = shape,
        minHeight = minHeight,
        contentSpacing = contentSpacing,
        flowRowSpacing = flowRowSpacing,
        dotSize = dotSize,
        dotBorderWidth = dotBorderWidth,
        horizontalPadding = horizontalPadding,
        locationMaxLines = locationMaxLines,
    )
}

/**
 * Single-address item rendered as a [Button][androidx.compose.material3.Button].
 *
 * Displays a single address text with optional leading and trailing content.
 * Use for simple address display where only one location is shown.
 *
 * ## Usage
 *
 * ```kotlin
 * AddressItem(
 *     text = "123 Main Street",
 *     onClick = { openMap() },
 *     leadingContent = { AddressDot(color = System.color.icon.brand) },
 * )
 * ```
 *
 * @param text Address text to display.
 * @param onClick Called when the item is clicked.
 * @param modifier Applied to the root button.
 * @param leadingContent Optional composable before the text (e.g., [AddressDot]).
 * @param trailingContent Optional composable after the text.
 * @param colors Color configuration, defaults to [AddressItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [AddressItemDefaults.dimens].
 *
 * @see AddressDot
 * @see AddressItemDefaults
 * @since 0.0.1
 */
@Composable
fun AddressItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: AddressItemColors = AddressItemDefaults.colors(),
    dimens: AddressItemDimens = AddressItemDefaults.dimens(),
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = dimens.minHeight),
        shape = dimens.shape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = colors.container,
            ),
        contentPadding =
            PaddingValues(
                start = dimens.horizontalPadding,
                end = 8.dp,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
        ) {
            leadingContent?.invoke()

            Text(
                text = text,
                color = colors.location,
                style = System.font.body.base.bold,
                modifier = Modifier.weight(1f),
            )

            trailingContent?.invoke()
        }
    }
}

/**
 * Multi-location address item rendered as a [Card][androidx.compose.material3.Card].
 *
 * When [locations] is empty, shows the [placeholder] text. When populated,
 * displays locations in a [FlowRow][androidx.compose.foundation.layout.FlowRow]
 * separated by forward arrows.
 *
 * ## Usage
 *
 * ```kotlin
 * AddressItem(
 *     locations = listOf("Home", "Work"),
 *     placeholder = "Where to?",
 *     onClick = { openSearchSheet() },
 *     leadingContent = { AddressDot(color = System.color.icon.brand) },
 * )
 * ```
 *
 * @param locations List of location names to display. Empty shows [placeholder].
 * @param placeholder Text shown when [locations] is empty.
 * @param onClick Called when the item is clicked.
 * @param modifier Applied to the root card.
 * @param leadingContent Optional composable before the locations.
 * @param trailingContent Optional composable after the locations.
 * @param colors Color configuration, defaults to [AddressItemDefaults.colors].
 * @param dimens Dimension configuration, defaults to [AddressItemDefaults.dimens].
 *
 * @see AddressDot
 * @see AddressItemDefaults
 * @since 0.0.1
 */
@Composable
fun AddressItem(
    locations: List<String>,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: AddressItemColors = AddressItemDefaults.colors(),
    dimens: AddressItemDimens = AddressItemDefaults.dimens(),
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = dimens.shape,
        colors =
            CardDefaults.cardColors(
                containerColor = colors.container,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier =
                Modifier
                    .heightIn(min = dimens.minHeight)
                    .padding(start = dimens.horizontalPadding, end = 8.dp),
        ) {
            leadingContent?.invoke()

            if (locations.isEmpty()) {
                Text(
                    text = placeholder,
                    color = colors.placeholder,
                    style = System.font.body.base.bold,
                    modifier = Modifier.weight(1f),
                )
            } else {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimens.flowRowSpacing),
                    horizontalArrangement = Arrangement.spacedBy(dimens.flowRowSpacing),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    locations.forEachIndexed { index, location ->
                        Text(
                            text = location,
                            color = colors.location,
                            style = System.font.body.base.bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = dimens.locationMaxLines,
                        )

                        if (index != locations.lastIndex) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                contentDescription = null,
                                tint = colors.arrow,
                            )
                        }
                    }
                }
            }

            trailingContent?.invoke()
        }
    }
}

/**
 * Colored dot indicator for address items.
 *
 * Renders a small circle with a colored border over a white fill. Used as the
 * leading indicator in [AddressItem] to distinguish origin from destination.
 *
 * @param color Border color of the dot (e.g., brand color for origin, red for destination).
 * @param modifier Applied to the dot.
 * @param dimens Dimension configuration providing dot size and border width.
 *
 * @see AddressItem
 * @since 0.0.1
 */
@Composable
fun AddressDot(
    color: Color,
    modifier: Modifier = Modifier,
    dimens: AddressItemDimens = AddressItemDefaults.dimens(),
) {
    Box(
        modifier =
            modifier
                .size(dimens.dotSize)
                .background(
                    color = System.color.icon.white,
                    shape = CircleShape,
                ).border(
                    width = dimens.dotBorderWidth,
                    color = color,
                    shape = CircleShape,
                ),
    )
}
