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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * State for [LocationItem] component.
 *
 * @property locations List of location names.
 * @property placeholder Text when locations is empty.
 */
data class LocationItemState(
    val locations: List<String>,
    val placeholder: String
)

/**
 * Simple location item with single text and optional icons.
 *
 * Use for simple location input fields that show a single text value.
 *
 * ## Usage
 *
 * ```kotlin
 * LocationItem(
 *     text = "Enter destination",
 *     onClick = { openSearch() },
 *     leadingContent = { Icon(Icons.Default.Search, null) },
 * )
 * ```
 *
 * @param text Display text.
 * @param onClick Called when item is clicked.
 * @param modifier Applied to item.
 * @param leadingContent Optional leading slot.
 * @param trailingContent Optional trailing slot.
 * @param colors Color configuration.
 * @param style Text style configuration.
 * @param dimens Dimension configuration.
 */
@Composable
fun LocationItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: LocationItemDefaults.LocationItemColors = LocationItemDefaults.colors(),
    style: LocationItemDefaults.LocationItemStyle = LocationItemDefaults.style(),
    dimens: LocationItemDefaults.LocationItemDimens = LocationItemDefaults.dimens(),
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
                style = style.location,
                modifier = Modifier.weight(1f),
            )

            trailingContent?.invoke()
        }
    }
}

/**
 * Location selector item showing multiple locations in flow layout.
 *
 * Displays locations with arrows between them or placeholder when empty.
 *
 * ## Usage
 *
 * ```kotlin
 * LocationItem(
 *     state = LocationItemState(
 *         locations = listOf("Home", "Office"),
 *         placeholder = "Where to?"
 *     ),
 *     onClick = { openLocationPicker() },
 *     leadingContent = { LocationDot(color = System.color.buttonActive) },
 *     trailingContent = { AddButton(onClick = { addStop() }) },
 * )
 * ```
 *
 * @param state Item state containing locations and placeholder.
 * @param onClick Called when item is clicked.
 * @param modifier Applied to item.
 * @param leadingContent Optional leading slot.
 * @param trailingContent Optional trailing slot.
 * @param colors Color configuration, defaults to [LocationItemDefaults.colors].
 * @param style Text style configuration, defaults to [LocationItemDefaults.style].
 * @param dimens Dimension configuration, defaults to [LocationItemDefaults.dimens].
 *
 * @see LocationItemDefaults for default values
 */
@Composable
fun LocationItem(
    state: LocationItemState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    colors: LocationItemDefaults.LocationItemColors = LocationItemDefaults.colors(),
    style: LocationItemDefaults.LocationItemStyle = LocationItemDefaults.style(),
    dimens: LocationItemDefaults.LocationItemDimens = LocationItemDefaults.dimens(),
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

            if (state.locations.isEmpty()) {
                Text(
                    text = state.placeholder,
                    color = colors.placeholder,
                    style = style.placeholder,
                    modifier = Modifier.weight(1f),
                )
            } else {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimens.flowRowSpacing),
                    horizontalArrangement = Arrangement.spacedBy(dimens.flowRowSpacing),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    state.locations.forEachIndexed { index, location ->
                        Text(
                            text = location,
                            color = colors.location,
                            style = style.location,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = dimens.locationMaxLines,
                        )

                        if (index != state.locations.lastIndex) {
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
 * Colored dot indicator for location items.
 *
 * @param color Dot border color.
 * @param modifier Applied to dot.
 * @param dimens Dimension configuration, defaults to [LocationItemDefaults.dimens].
 */
@Composable
fun LocationDot(
    color: Color,
    modifier: Modifier = Modifier,
    dimens: LocationItemDefaults.LocationItemDimens = LocationItemDefaults.dimens(),
) {
    Box(
        modifier =
            modifier
                .size(dimens.dotSize)
                .background(
                    color = System.color.iconWhite,
                    shape = CircleShape,
                ).border(
                    width = dimens.dotBorderWidth,
                    color = color,
                    shape = CircleShape,
                ),
    )
}

/**
 * Default configuration values for [LocationItem].
 *
 * Provides theme-aware defaults for [colors], [style], and [dimens] that can be overridden.
 */
object LocationItemDefaults {
    /**
     * Color configuration for [LocationItem].
     *
     * @param container Background color.
     * @param placeholder Placeholder text color.
     * @param location Location text color.
     * @param arrow Arrow icon tint.
     */
    data class LocationItemColors(
        val container: Color,
        val placeholder: Color,
        val location: Color,
        val arrow: Color,
    )

    @Composable
    fun colors(
        container: Color = System.color.backgroundSecondary,
        placeholder: Color = System.color.textSubtle,
        location: Color = System.color.textBase,
        arrow: Color = System.color.iconSubtle,
    ) = LocationItemColors(
        container = container,
        placeholder = placeholder,
        location = location,
        arrow = arrow,
    )

    /**
     * Text style configuration for [LocationItem].
     *
     * @param placeholder Placeholder text style.
     * @param location Location text style.
     */
    data class LocationItemStyle(
        val placeholder: TextStyle,
        val location: TextStyle,
    )

    @Composable
    fun style(
        placeholder: TextStyle = System.font.body.base.bold,
        location: TextStyle = System.font.body.base.bold,
    ) = LocationItemStyle(
        placeholder = placeholder,
        location = location,
    )

    /**
     * Dimension configuration for [LocationItem].
     *
     * @param shape Item shape.
     * @param minHeight Minimum item height.
     * @param contentSpacing Spacing between elements.
     * @param flowRowSpacing Spacing in flow row.
     * @param dotSize Size of location dot indicator.
     * @param dotBorderWidth Border width of dot indicator.
     * @param horizontalPadding Horizontal padding.
     * @param locationMaxLines Maximum lines for location text.
     */
    data class LocationItemDimens(
        val shape: Shape,
        val minHeight: Dp,
        val contentSpacing: Dp,
        val flowRowSpacing: Dp,
        val dotSize: Dp,
        val dotBorderWidth: Dp,
        val horizontalPadding: Dp,
        val locationMaxLines: Int,
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        minHeight: Dp = 60.dp,
        contentSpacing: Dp = 12.dp,
        flowRowSpacing: Dp = 6.dp,
        dotSize: Dp = 14.dp,
        dotBorderWidth: Dp = 4.dp,
        horizontalPadding: Dp = 16.dp,
        locationMaxLines: Int = 1,
    ) = LocationItemDimens(
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

@Preview
@Composable
private fun LocationItemPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        LocationItem(
            state =
                LocationItemState(
                    locations = listOf("Home", "Office"),
                    placeholder = "Where to?"
                ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun LocationItemEmptyPreview() {
    Box(
        modifier =
            Modifier
                .background(Color.White)
                .padding(16.dp)
    ) {
        LocationItem(
            state =
                LocationItemState(
                    locations = emptyList(),
                    placeholder = "Where to?"
                ),
            onClick = {},
        )
    }
}
