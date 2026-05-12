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
import uz.yalla.resources.icons.FocusDestination
import uz.yalla.resources.icons.FocusOrigin
import uz.yalla.resources.icons.ThemeLight
import uz.yalla.resources.icons.Unchecked
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
    val indicatorColor: Color,
    val selectedIndicatorColor: Color,
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

    @Composable
    fun indicatorColorFor(selected: Boolean) =
        if (selected) selectedIndicatorColor else indicatorColor
}

@Immutable
data class SelectableItemDimens(
    val shape: Shape,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues,
    val iconSize: Dp,
    val indicatorSize: Dp,
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
        selectedBorderColor: Color = Color.Transparent,
        indicatorColor: Color = Color.Unspecified,
        selectedIndicatorColor: Color = Color.Unspecified
    ) = SelectableItemColors(
        iconColor = iconColor,
        textColor = textColor,
        containerColor = containerColor,
        borderColor = borderColor,
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        selectedContainerColor = selectedContainerColor,
        selectedBorderColor = selectedBorderColor,
        indicatorColor = indicatorColor,
        selectedIndicatorColor = selectedIndicatorColor
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
        indicatorSize: Dp = 24.dp,
        borderWidth: Dp = 1.dp
    ) = SelectableItemDimens(
        shape = shape,
        contentSpacing = contentSpacing,
        contentPadding = contentPadding,
        iconSize = iconSize,
        indicatorSize = indicatorSize,
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
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingPainter: Painter? = null,
    selectedIndicatorPainter: Painter? = rememberVectorPainter(YallaIcons.Checked),
    unselectedIndicatorPainter: Painter? = null,
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            leadingPainter?.let { painter ->
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

            if (selected) selectedIndicatorPainter?.let { painter ->
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = colors.indicatorColorFor(selected),
                    modifier = Modifier.size(dimens.indicatorSize)
                )
            }
            else unselectedIndicatorPainter?.let { painter ->
                Icon(
                    painter = painter,
                    contentDescription = null,
                    tint = colors.indicatorColorFor(selected),
                    modifier = Modifier.size(dimens.indicatorSize)
                )
            }
        }
    }
}

@Composable
fun SelectableItem(
    text: String,
    selected: Boolean,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedIndicatorPainter: Painter? = rememberVectorPainter(YallaIcons.Checked),
    unselectedIndicatorPainter: Painter? = null,
    colors: SelectableItemColors = SelectableItemDefaults.colors(),
    dimens: SelectableItemDimens = SelectableItemDefaults.dimens(),
    styles: SelectableItemStyles = SelectableItemDefaults.styles()
) = SelectableItem(
    text = text,
    selected = selected,
    onClick = onClick,
    modifier = modifier,
    leadingPainter = rememberVectorPainter(imageVector),
    selectedIndicatorPainter = selectedIndicatorPainter,
    unselectedIndicatorPainter = unselectedIndicatorPainter,
    colors = colors,
    dimens = dimens,
    styles = styles
)

@Composable
@Preview
private fun Preview() = YallaTheme {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectableItem(
                text = "O'zbek tili",
                selected = false,
                imageVector = YallaIcons.FlagUz,
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            )

            SelectableItem(
                text = "Day mode",
                selected = true,
                leadingPainter = rememberVectorPainter(YallaIcons.ThemeLight),
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                dimens = SelectableItemDefaults.dimens(iconSize = 24.dp)
            )

            SelectableItem(
                text = "Day mode",
                selected = true,
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                dimens = SelectableItemDefaults.dimens(iconSize = 24.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectableItem(
                text = "Male",
                selected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
                unselectedIndicatorPainter = rememberVectorPainter(YallaIcons.Unchecked),
                colors = SelectableItemDefaults.colors(
                    textColor = System.color.text.subtle,
                    containerColor = System.color.background.secondary,
                    selectedContainerColor = System.color.background.base,
                    borderColor = Color.Transparent
                ),
                dimens = SelectableItemDefaults.dimens(
                    contentSpacing = System.space.scale.xs,
                    contentPadding = PaddingValues(
                        start = System.space.scale.xl,
                        top = System.space.scale.m,
                        end = System.space.scale.m,
                        bottom = System.space.scale.m
                    ),
                    iconSize = 24.dp,
                    borderWidth = 0.dp
                )
            )

            SelectableItem(
                text = "Female",
                selected = false,
                onClick = {},
                modifier = Modifier.weight(1f),
                unselectedIndicatorPainter = rememberVectorPainter(YallaIcons.Unchecked),
                colors = SelectableItemDefaults.colors(
                    textColor = System.color.text.subtle,
                    containerColor = System.color.background.secondary,
                    selectedContainerColor = System.color.background.base,
                    borderColor = Color.Transparent
                ),
                dimens = SelectableItemDefaults.dimens(
                    contentSpacing = System.space.scale.xs,
                    contentPadding = PaddingValues(
                        start = System.space.scale.xl,
                        top = System.space.scale.m,
                        end = System.space.scale.m,
                        bottom = System.space.scale.m
                    ),
                    iconSize = 24.dp,
                    borderWidth = 0.dp
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectableItem(
                text = "Baggage",
                selected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
                leadingPainter = rememberVectorPainter(YallaIcons.FocusOrigin),
                unselectedIndicatorPainter = rememberVectorPainter(YallaIcons.Unchecked),
                colors = SelectableItemDefaults.colors(
                    textColor = System.color.text.subtle,
                    containerColor = System.color.background.secondary,
                    selectedContainerColor = System.color.background.base,
                    borderColor = Color.Transparent
                ),
                dimens = SelectableItemDefaults.dimens(
                    contentSpacing = System.space.scale.xs,
                    contentPadding = PaddingValues(
                        start = System.space.scale.m,
                        top = System.space.scale.s,
                        end = System.space.scale.m,
                        bottom = System.space.scale.s
                    ),
                    iconSize = 24.dp,
                    borderWidth = 0.dp
                )
            )

            SelectableItem(
                text = "Conditioner",
                selected = false,
                onClick = {},
                modifier = Modifier.weight(1f),
                leadingPainter = rememberVectorPainter(YallaIcons.FocusDestination),
                unselectedIndicatorPainter = rememberVectorPainter(YallaIcons.Unchecked),
                colors = SelectableItemDefaults.colors(
                    textColor = System.color.text.subtle,
                    containerColor = System.color.background.secondary,
                    selectedContainerColor = System.color.background.base,
                    borderColor = Color.Transparent
                ),
                dimens = SelectableItemDefaults.dimens(
                    contentSpacing = System.space.scale.xs,
                    contentPadding = PaddingValues(
                        start = System.space.scale.m,
                        top = System.space.scale.s,
                        end = System.space.scale.m,
                        bottom = System.space.scale.s
                    ),
                    iconSize = 24.dp,
                    borderWidth = 0.dp
                )
            )
        }
    }
}
