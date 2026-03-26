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

@Immutable
data class AddressItemColors(
    val container: Color,
    val placeholder: Color,
    val location: Color,
    val arrow: Color,
)

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

object AddressItemDefaults {

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
