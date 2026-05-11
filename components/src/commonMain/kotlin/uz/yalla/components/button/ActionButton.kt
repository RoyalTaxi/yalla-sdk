package uz.yalla.components.button

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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.resources.icons.Camera
import uz.yalla.resources.icons.YallaIcons

@Immutable
data class ActionButtonColors(
    val textColor: Color,
    val iconColor: Color,
    val containerColor: Color,
    val disabledTextColor: Color,
    val disabledIconColor: Color,
    val disabledContainerColor: Color
) {
    @Composable
    fun textColorFor(enabled: Boolean) =
        if (enabled) textColor else disabledTextColor

    @Composable
    fun iconColorFor(enabled: Boolean) =
        if (enabled) iconColor else disabledIconColor

    @Composable
    fun containerColorFor(enabled: Boolean) =
        if (enabled) containerColor else disabledContainerColor
}

@Immutable
data class ActionButtonDimens(
    val shape: Shape,
    val contentSpacing: Dp,
    val contentPadding: PaddingValues,
    val iconSize: Dp
)

@Immutable
data class ActionButtonStyles(
    val textStyle: TextStyle
)

object ActionButtonDefaults {
    @Composable
    fun colors(
        textColor: Color = System.color.text.base,
        iconColor: Color = System.color.button.tertiary,
        containerColor: Color = System.color.background.secondary,
        disabledTextColor: Color = System.color.text.base,
        disabledIconColor: Color = System.color.button.tertiary,
        disabledContainerColor: Color = System.color.background.secondary
    ) = ActionButtonColors(
        textColor = textColor,
        iconColor = iconColor,
        containerColor = containerColor,
        disabledTextColor = disabledTextColor,
        disabledIconColor = disabledIconColor,
        disabledContainerColor = disabledContainerColor
    )

    @Composable
    fun dimens(
        shape: Shape = RoundedCornerShape(System.radius.l),
        contentSpacing: Dp = System.space.scale.m,
        contentPadding: PaddingValues = PaddingValues(System.space.scale.xl),
        iconSize: Dp = 24.dp,
    ) = ActionButtonDimens(
        shape = shape,
        contentSpacing = contentSpacing,
        contentPadding = contentPadding,
        iconSize = iconSize,
    )

    @Composable
    fun styles(
        textStyle: TextStyle = System.font.body.base.medium
    ) = ActionButtonStyles(
        textStyle = textStyle
    )
}

@Composable
fun ActionButton(
    text: String,
    painter: Painter,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: ActionButtonColors = ActionButtonDefaults.colors(),
    dimens: ActionButtonDimens = ActionButtonDefaults.dimens(),
    styles: ActionButtonStyles = ActionButtonDefaults.styles()
) {
    Surface(
        enabled = enabled,
        modifier = modifier,
        onClick = onClick,
        color = colors.containerColorFor(enabled),
        shape = dimens.shape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.contentSpacing),
            modifier = Modifier.padding(dimens.contentPadding)
        ) {
            Icon(
                painter = painter,
                tint = colors.iconColorFor(enabled),
                contentDescription = null,
                modifier = Modifier.size(dimens.iconSize)
            )

            Text(
                text = text,
                color = colors.textColorFor(enabled),
                style = styles.textStyle
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
        ActionButton(
            text = "Take a picture",
            modifier = Modifier.fillMaxWidth(),
            painter = rememberVectorPainter(YallaIcons.Camera),
            enabled = true,
            onClick = {}
        )

        ActionButton(
            text = "Choose from gallery",
            modifier = Modifier.fillMaxWidth(),
            painter = rememberVectorPainter(YallaIcons.Camera),
            enabled = false,
            onClick = {}
        )

        ActionButton(
            text = "Take a picture",
            modifier = Modifier.fillMaxWidth(),
            painter = rememberVectorPainter(YallaIcons.Camera),
            enabled = true,
            onClick = {},
            colors = ActionButtonDefaults.colors(disabledIconColor = System.color.icon.red)
        )

        ActionButton(
            text = "Choose from gallery",
            modifier = Modifier.fillMaxWidth(),
            painter = rememberVectorPainter(YallaIcons.Camera),
            enabled = false,
            onClick = {},
            colors = ActionButtonDefaults.colors(disabledIconColor = System.color.icon.red)
        )
    }
}
