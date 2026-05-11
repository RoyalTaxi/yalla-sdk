package uz.yalla.components.item

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.FlagUz
import uz.yalla.resources.icons.ThemeLight
import uz.yalla.resources.icons.YallaIcons

@Immutable
data class SelectableItemColors(
    val iconColor: Color,
    val textColor: Color,
    val containerColor: Color,
    val borderColor: Color,
    val selectedIconColor: Color,
    val selectedTextColor: Color,
    val selectedContainerColor: Color,
    val selectedBorderColor: Color,
) {
    @Composable
    fun iconColorFor(selected: Boolean) =
        if (selected) selectedIconColor else iconColor

    @Composable
    fun textColorFor(selected: Boolean) =
        if (selected) selectedTextColor else textColor

    @Composable
    fun containerColorFor(selected: Boolean) =
        if (selected) selectedContainerColor else containerColor

    @Composable
    fun borderColorFor(selected: Boolean) =
        if (selected) selectedBorderColor else borderColor
}

@Immutable
data class SelectableItemDimens(
    val shape: Shape,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val borderWidth: Dp
)

@Immutable
data class SelectableItemStyles(
    val textStyle: TextStyle
)

object SelectableItemDefaults {
    @Composable
    fun colors(
        iconColor: Color = Color.Unspecified,
        textColor: Color = System.color.text.base,
        containerColor: Color = Color.Transparent,
        borderColor: Color = System.color.border.disabled,
        selectedIconColor: Color = Color.Unspecified,
        selectedTextColor: Color = System.color.text.base,
        selectedContainerColor: Color = System.color.background.secondary,
        selectedBorderColor: Color = Color.Transparent
    ) = SelectableItemColors(
        iconColor = iconColor,
        textColor = textColor,
        containerColor = containerColor,
        borderColor = borderColor,
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        selectedContainerColor = selectedContainerColor,
        selectedBorderColor = selectedBorderColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(System.radius.l),
        contentSpacing: Dp = System.space.scale.s,
        contentPadding: PaddingValues = PaddingValues(
            vertical = System.space.scale.m,
            horizontal = System.space.scale.l
        ),
        iconSize: Dp = 34.dp,
        borderWidth: Dp = 1.dp
    ) = SelectableItemDimens(
        shape = shape,
        contentSpacing = contentSpacing,
        contentPadding = contentPadding,
        iconSize = iconSize,
        borderWidth = borderWidth
    )

    @Composable
    fun styles(
        textStyle: TextStyle = System.font.body.small.medium
    ) = SelectableItemStyles(
        textStyle = textStyle
    )
}

@Composable
fun SelectableItem(
    selected: Boolean,
    painter: Painter?,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SelectableItemColors = SelectableItemDefaults.colors(),
    dimens: SelectableItemDimens = SelectableItemDefaults.dimens(),
    styles: SelectableItemStyles = SelectableItemDefaults.styles()
) {
    Surface(
        color = colors.containerColorFor(selected),
        shape = dimens.shape,
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(
            width = dimens.borderWidth,
            color = colors.borderColorFor(selected)
        )
    ) {
        Row(
            modifier = Modifier.padding(dimens.contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing)
        ) {
            painter?.let {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = colors.iconColorFor(selected),
                    modifier = Modifier.size(dimens.iconSize)
                )
            }

            Text(
                text = text,
                color = colors.textColorFor(selected),
                style = styles.textStyle,
                modifier = Modifier.weight(1f)
            )

            if (selected)
                Icon(
                    painter = rememberVectorPainter(YallaIcons.Checked),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
        }
    }
}

@Composable
fun SelectableItem(
    selected: Boolean,
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: SelectableItemColors = SelectableItemDefaults.colors(),
    dimens: SelectableItemDimens = SelectableItemDefaults.dimens()
) = SelectableItem(
    selected = selected,
    painter = rememberVectorPainter(imageVector),
    text = text,
    onClick = onClick,
    modifier = modifier,
    colors = colors,
    dimens = dimens
)

@Composable
@Preview
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelectableItem(
            selected = false,
            imageVector = YallaIcons.FlagUz,
            text = "O'zbek tili",
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = SelectableItemDefaults.colors()
        )

        SelectableItem(
            selected = true,
            painter = rememberVectorPainter(YallaIcons.ThemeLight),
            text = "Day mode",
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = SelectableItemDefaults.colors(),
            dimens = SelectableItemDefaults.dimens(iconSize = 24.dp)
        )

        SelectableItem(
            selected = true,
            painter = null,
            text = "Day mode",
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            colors = SelectableItemDefaults.colors(),
            dimens = SelectableItemDefaults.dimens(iconSize = 24.dp)
        )
    }
}