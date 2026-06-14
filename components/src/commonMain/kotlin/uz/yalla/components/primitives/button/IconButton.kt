package uz.yalla.components.primitives.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.design.theme.System

public enum class IconButtonShape {
    CIRCLE,
    SQUIRCLE,
}

@Composable
public expect fun IconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: IconButtonShape = IconButtonShape.CIRCLE,
    iconColor: Color = System.color.icon.base,
    containerColor: Color = System.color.background.secondary,
    borderColor: Color = Color.Unspecified
)
