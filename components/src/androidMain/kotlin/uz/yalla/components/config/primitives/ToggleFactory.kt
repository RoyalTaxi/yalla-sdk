package uz.yalla.components.config.primitives

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

interface ToggleFactory {
    @Composable
    fun Content(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        enabled: Boolean,
        checkedThumbColor: Color,
        checkedTrackColor: Color,
        checkedBorderColor: Color,
        uncheckedThumbColor: Color,
        uncheckedTrackColor: Color,
        uncheckedBorderColor: Color,
        disabledCheckedThumbColor: Color,
        disabledCheckedTrackColor: Color,
        disabledCheckedBorderColor: Color,
        disabledUncheckedThumbColor: Color,
        disabledUncheckedTrackColor: Color,
        disabledUncheckedBorderColor: Color,
        modifier: Modifier
    )
}
