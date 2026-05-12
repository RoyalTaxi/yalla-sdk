package uz.yalla.components.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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
    val openIconColor: Color
)

@Immutable
data class OpenableItemDimens(
    val shape: Shape,
    val iconSize: Dp,
    val contentPadding: PaddingValues
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
        openIconColor: Color = System.color.icon.subtle
    ) = OpenableItemColors(
        textColor = textColor,
        containerColor = containerColor,
        openIconColor = openIconColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(System.radius.l),
        iconSize: Dp = 18.dp,
        contentPadding: PaddingValues = PaddingValues(
            start = System.space.scale.l,
            top = System.space.scale.l,
            end = System.space.scale.m,
            bottom = System.space.scale.l
        )
    ) = OpenableItemDimens(
        shape = shape,
        iconSize = iconSize,
        contentPadding = contentPadding
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
    colors: OpenableItemColors = OpenableItemDefaults.colors(),
    dimens: OpenableItemDimens = OpenableItemDefaults.dimens(),
    styles: OpenableItemStyles = OpenableItemDefaults.styles()
) {
    Surface(
        shape = dimens.shape,
        color = colors.containerColor,
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            Text(
                text = text,
                style = styles.textStyle,
                color = colors.textColor
            )

            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
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
    }
}