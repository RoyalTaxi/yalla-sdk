package uz.yalla.components.button

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
data class ActionButtonColors(
    val iconColor: Color,
)

object ActionButtonDefaults {
    @Composable
    fun colors(
        iconColor: Color = System.color.button.tertiary
    ) = ActionButtonColors(
        iconColor = iconColor
    )
}

@Composable
fun ActionButton(
    text: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    colors: ActionButtonColors = ActionButtonDefaults.colors()
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = System.color.background.secondary,
        shape = RoundedCornerShape(System.radius.l)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(System.space.scale.m),
            modifier = Modifier.padding(System.space.scale.xl)
        ) {
            Icon(
                painter = painter,
                tint = colors.iconColor,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = text,
                color = System.color.text.base,
                style = System.font.body.base.medium
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
            onClick = {}
        )

        ActionButton(
            text = "Choose from gallery",
            modifier = Modifier.fillMaxWidth(),
            painter = rememberVectorPainter(YallaIcons.Gallery),
            onClick = {},
            colors = ActionButtonDefaults.colors(System.color.icon.red)
        )
    }
}
