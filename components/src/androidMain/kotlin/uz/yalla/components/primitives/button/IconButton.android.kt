package uz.yalla.components.primitives.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.components.config.requireConfig

@Composable
actual fun IconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier,
    shape: IconButtonShape,
    iconColor: Color,
    containerColor: Color,
    borderColor: Color
) {
    requireConfig().iconButton.Content(
        icon = icon,
        shape = shape,
        iconColor = iconColor,
        containerColor = containerColor,
        borderColor = borderColor,
        onClick = onClick,
        modifier = modifier
    )
}
