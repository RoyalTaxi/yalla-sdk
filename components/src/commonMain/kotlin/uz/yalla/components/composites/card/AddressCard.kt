package uz.yalla.components.composites.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Location
import uz.yalla.resources.icons.Origin
import uz.yalla.resources.icons.YallaIcons

@Immutable
public data class AddressCardColors(
    val borderColor: Color,
    val containerColor: Color,
    val textColor: Color,
    val placeholderColor: Color,
    val trailingContainerColor: Color,
    val trailingIconColor: Color
)

@Immutable
public data class AddressCardDimens(
    val shape: Shape,
    val borderWidth: Dp,
    val contentPadding: PaddingValues,
    val contentSpacing: Dp,
    val leadingIconSize: Dp,
    val trailingButtonSize: Dp,
    val trailingButtonShape: Shape,
    val trailingIconSize: Dp
)

@Immutable
public data class AddressCardStyles(
    val textStyle: TextStyle
)

public object AddressCardDefaults {
    @Composable
    public fun colors(
        borderColor: Color = System.color.border.disabled,
        containerColor: Color = Color.Transparent,
        textColor: Color = System.color.text.base,
        placeholderColor: Color = System.color.text.subtle,
        trailingContainerColor: Color = System.color.background.secondary,
        trailingIconColor: Color = System.color.icon.base
    ): AddressCardColors = AddressCardColors(
        borderColor = borderColor,
        containerColor = containerColor,
        textColor = textColor,
        placeholderColor = placeholderColor,
        trailingContainerColor = trailingContainerColor,
        trailingIconColor = trailingIconColor
    )

    @Composable
    public fun dimens(
        shape: Shape = RoundedCornerShape(12.dp),
        borderWidth: Dp = 1.dp,
        contentPadding: PaddingValues = PaddingValues(start = 12.dp, top = 4.dp, end = 4.dp, bottom = 4.dp),
        contentSpacing: Dp = 8.dp,
        leadingIconSize: Dp = 14.dp,
        trailingButtonSize: Dp = 40.dp,
        trailingButtonShape: Shape = RoundedCornerShape(10.dp),
        trailingIconSize: Dp = 20.dp
    ): AddressCardDimens = AddressCardDimens(
        shape = shape,
        borderWidth = borderWidth,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        leadingIconSize = leadingIconSize,
        trailingButtonSize = trailingButtonSize,
        trailingButtonShape = trailingButtonShape,
        trailingIconSize = trailingIconSize
    )

    @Composable
    public fun styles(
        textStyle: TextStyle = System.font.body.base.medium
    ): AddressCardStyles = AddressCardStyles(
        textStyle = textStyle
    )
}

@Composable
public fun AddressCard(
    value: String?,
    placeholder: String,
    leadingPainter: Painter,
    trailingPainter: Painter,
    onClick: () -> Unit,
    onTrailingClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: AddressCardColors = AddressCardDefaults.colors(),
    dimens: AddressCardDimens = AddressCardDefaults.dimens(),
    styles: AddressCardStyles = AddressCardDefaults.styles()
) {
    Surface(
        onClick = onClick,
        shape = dimens.shape,
        color = colors.containerColor,
        border = BorderStroke(dimens.borderWidth, colors.borderColor),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.contentPadding)
        ) {
            Image(
                painter = leadingPainter,
                contentDescription = null,
                modifier = Modifier.size(dimens.leadingIconSize)
            )

            Text(
                text = value ?: placeholder,
                color = if (value == null) colors.placeholderColor else colors.textColor,
                style = styles.textStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                modifier = Modifier.weight(1f)
            )

            Surface(
                onClick = onTrailingClick,
                shape = dimens.trailingButtonShape,
                color = colors.trailingContainerColor,
                modifier = Modifier.size(dimens.trailingButtonSize)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = trailingPainter,
                        contentDescription = null,
                        tint = colors.trailingIconColor,
                        modifier = Modifier.size(dimens.trailingIconSize)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .background(System.color.background.base)
            .padding(16.dp)
    ) {
        AddressCard(
            value = null,
            placeholder = "Enter address",
            leadingPainter = rememberVectorPainter(YallaIcons.Origin),
            trailingPainter = rememberVectorPainter(YallaIcons.Location),
            onClick = {},
            onTrailingClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        AddressCard(
            value = "Amir Temur 1, Tashkent",
            placeholder = "Enter address",
            leadingPainter = rememberVectorPainter(YallaIcons.Origin),
            trailingPainter = rememberVectorPainter(YallaIcons.Location),
            onClick = {},
            onTrailingClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        AddressCard(
            value = "Mustaqillik shoh ko'chasi 12-uy, 4-qavat, Yashnabod tumani, Toshkent shahri, 100000",
            placeholder = "Enter address",
            leadingPainter = rememberVectorPainter(YallaIcons.Origin),
            trailingPainter = rememberVectorPainter(YallaIcons.Location),
            onClick = {},
            onTrailingClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
