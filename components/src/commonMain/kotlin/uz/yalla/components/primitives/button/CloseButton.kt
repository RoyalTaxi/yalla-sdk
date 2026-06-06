package uz.yalla.components.primitives.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.design.theme.System

@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = IconButton(
    icon = "ic_x",
    onClick = onClick,
    modifier = modifier,
    shape = IconButtonShape.CIRCLE,
    iconColor = System.color.icon.base,
    containerColor = Color.Transparent,
    borderColor = System.color.border.disabled
)
