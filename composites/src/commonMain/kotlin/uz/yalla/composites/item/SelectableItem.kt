package uz.yalla.composites.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.resources.icons.Checked
import uz.yalla.resources.icons.YallaIcons

@Immutable
data class SelectableItemColors(
    val selectedContainer: Color,
    val unselectedContainer: Color,
    val border: Color,
    val text: Color,
)

@Immutable
data class SelectableItemDimens(
    val shape: Shape,
    val contentPadding: PaddingValues,
    val iconSpacing: Dp,
    val borderWidth: Dp,
)

object SelectableItemDefaults {

    @Composable
    fun colors(
        selectedContainer: Color = System.color.background.secondary,
        unselectedContainer: Color = Color.Transparent,
        border: Color = System.color.border.disabled,
        text: Color = System.color.text.base,
    ) = SelectableItemColors(
        selectedContainer = selectedContainer,
        unselectedContainer = unselectedContainer,
        border = border,
        text = text,
    )

    fun dimens(
        shape: Shape = RoundedCornerShape(16.dp),
        contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        iconSpacing: Dp = 12.dp,
        borderWidth: Dp = 1.dp,
    ) = SelectableItemDimens(
        shape = shape,
        contentPadding = contentPadding,
        iconSpacing = iconSpacing,
        borderWidth = borderWidth,
    )
}

@Composable
fun SelectableItem(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    colors: SelectableItemColors = SelectableItemDefaults.colors(),
    dimens: SelectableItemDimens = SelectableItemDefaults.dimens(),
) {
    Surface(
        modifier = modifier,
        onClick = onSelect,
        shape = dimens.shape,
        color = if (isSelected) colors.selectedContainer else colors.unselectedContainer,
        border =
            BorderStroke(
                width = if (isSelected) 0.dp else dimens.borderWidth,
                color = colors.border,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimens.contentPadding),
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(dimens.iconSpacing))
            }

            Text(
                text = title,
                color = colors.text,
                style = System.font.body.small.medium,
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Icon(
                    painter = rememberVectorPainter(YallaIcons.Checked),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        }
    }
}
