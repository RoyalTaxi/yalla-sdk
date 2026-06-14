package uz.yalla.components.primitives.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.design.theme.System

@Composable
public fun DoneButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
): Unit =
    IconButton(
        icon = "ic_check",
        onClick = onClick,
        modifier = modifier,
        shape = IconButtonShape.CIRCLE,
        iconColor = System.color.icon.base,
        containerColor = Color.Transparent,
        borderColor = System.color.border.disabled
    )
