package uz.yalla.components.composites.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Camera
import uz.yalla.resources.icons.Gallery
import uz.yalla.resources.icons.YallaIcons

@Immutable
public data class ActionableItemModel(
    val id: String,
    val text: String,
    val icon: String,
    val trailingIcon: String? = null,
    val isDestructive: Boolean = false
)

@Immutable
public data class ActionableItemColors(
    val iconColor: Color,
    val textColor: Color,
    val trailingIconColor: Color
)

public object ActionableItemDefaults {
    @Composable
    public fun colors(
        iconColor: Color = System.color.icon.base,
        textColor: Color = System.color.text.base,
        trailingIconColor: Color = System.color.icon.base
    ): ActionableItemColors = ActionableItemColors(
        iconColor = iconColor,
        textColor = textColor,
        trailingIconColor = trailingIconColor
    )

    @Composable
    public fun colorsFor(model: ActionableItemModel): ActionableItemColors {
        if (!model.isDestructive) return colors()
        val redIcon = System.color.icon.red
        val redText = System.color.text.red
        val hasLeading = model.icon.isNotEmpty()
        val hasTrailing = !model.trailingIcon.isNullOrEmpty()
        return when {
            hasTrailing -> colors(trailingIconColor = redIcon)
            hasLeading -> colors(iconColor = redIcon)
            else -> colors(textColor = redText)
        }
    }
}

@Composable
public fun ActionableItem(
    text: String,
    painter: Painter?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    trailingPainter: Painter? = null,
    colors: ActionableItemColors = ActionableItemDefaults.colors()
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = System.color.background.secondary,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            painter?.let {
                Icon(
                    painter = it,
                    tint = colors.iconColor,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = text,
                color = colors.textColor,
                style = System.font.body.base.medium,
                modifier = Modifier.weight(1f)
            )

            trailingPainter?.let {
                Icon(
                    painter = it,
                    tint = colors.trailingIconColor,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
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
        ActionableItem(
            text = "Take a picture",
            modifier = Modifier.fillMaxWidth(),
            painter = rememberVectorPainter(YallaIcons.Camera),
            onClick = {}
        )

        ActionableItem(
            text = "Choose from gallery",
            modifier = Modifier.fillMaxWidth(),
            painter = rememberVectorPainter(YallaIcons.Gallery),
            onClick = {},
            colors = ActionableItemDefaults.colors(System.color.icon.red)
        )
    }
}
