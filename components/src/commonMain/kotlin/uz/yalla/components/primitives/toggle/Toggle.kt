package uz.yalla.components.primitives.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.design.theme.System

@Composable
public expect fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    checkedThumbColor: Color = System.color.icon.white,
    checkedTrackColor: Color = System.color.background.brand,
    checkedBorderColor: Color = System.color.background.brand,
    uncheckedThumbColor: Color = System.color.icon.white,
    uncheckedTrackColor: Color = System.color.icon.subtle,
    uncheckedBorderColor: Color = System.color.icon.subtle,
    disabledCheckedThumbColor: Color = System.color.icon.white.copy(alpha = 0.7f),
    disabledCheckedTrackColor: Color = System.color.background.brand.copy(alpha = 0.5f),
    disabledCheckedBorderColor: Color = System.color.background.brand.copy(alpha = 0.5f),
    disabledUncheckedThumbColor: Color = System.color.icon.white.copy(alpha = 0.7f),
    disabledUncheckedTrackColor: Color = System.color.icon.subtle.copy(alpha = 0.5f),
    disabledUncheckedBorderColor: Color = System.color.icon.subtle.copy(alpha = 0.5f)
)
