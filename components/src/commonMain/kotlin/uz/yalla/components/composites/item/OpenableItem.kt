package uz.yalla.components.composites.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import uz.yalla.resources.icons.ArrowRight
import uz.yalla.resources.icons.Trash
import uz.yalla.resources.icons.YallaIcons
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

@Immutable
data class OpenableItemColors(
    val textColor: Color,
    val containerColor: Color,
    val openIconColor: Color,
    val leadingIconColor: Color,
    val borderColor: Color
)

@Immutable
data class OpenableItemDimens(
    val shape: Shape,
    val iconSize: Dp,
    val contentPadding: PaddingValues,
    val contentSpacing: Dp,
    val borderWidth: Dp
)

@Immutable
data class OpenableItemStyles(
    val textStyle: TextStyle
)

object OpenableItemDefaults {
    @Composable
    fun colors(
        textColor: Color = System.color.text.base,
        containerColor: Color = System.color.background.secondary,
        openIconColor: Color = System.color.icon.subtle,
        leadingIconColor: Color = System.color.icon.base,
        borderColor: Color = Color.Transparent
    ) = OpenableItemColors(
        textColor = textColor,
        containerColor = containerColor,
        openIconColor = openIconColor,
        leadingIconColor = leadingIconColor,
        borderColor = borderColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        iconSize: Dp = 18.dp,
        contentPadding: PaddingValues = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 12.dp,
            bottom = 16.dp
        ),
        contentSpacing: Dp = 12.dp,
        borderWidth: Dp = 0.dp
    ) = OpenableItemDimens(
        shape = shape,
        iconSize = iconSize,
        contentPadding = contentPadding,
        contentSpacing = contentSpacing,
        borderWidth = borderWidth
    )

    @Composable
    fun styles(
        textStyle: TextStyle = System.font.body.base.medium
    ) = OpenableItemStyles(
        textStyle = textStyle
    )
}

@Composable
fun OpenableItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    colors: OpenableItemColors = OpenableItemDefaults.colors(),
    dimens: OpenableItemDimens = OpenableItemDefaults.dimens(),
    styles: OpenableItemStyles = OpenableItemDefaults.styles()
) {
    Surface(
        shape = dimens.shape,
        color = colors.containerColor,
        border = if (dimens.borderWidth > 0.dp) BorderStroke(dimens.borderWidth, colors.borderColor) else null,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = colors.leadingIconColor,
                    modifier = Modifier.size(dimens.iconSize)
                )
            }

            Text(
                text = text,
                style = styles.textStyle,
                color = colors.textColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = YallaIcons.ArrowRight,
                contentDescription = null,
                tint = colors.openIconColor,
                modifier = Modifier.size(dimens.iconSize)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OpenableItem(
            text = "Andijan region",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        OpenableItem(
            text = "Delete trip",
            onClick = {},
            leadingIcon = YallaIcons.Trash,
            colors = OpenableItemDefaults.colors(
                textColor = System.color.text.red,
                containerColor = Color.Transparent,
                openIconColor = System.color.icon.red,
                leadingIconColor = System.color.icon.red,
                borderColor = System.color.border.disabled
            ),
            dimens = OpenableItemDefaults.dimens(borderWidth = 1.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
