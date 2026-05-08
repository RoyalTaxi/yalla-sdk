package uz.yalla.platform.toggle

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.design.theme.System

@Composable
actual fun NativeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors =
            SwitchDefaults.colors(
                checkedThumbColor = System.color.icon.white,
                checkedTrackColor = System.color.background.brand,
                checkedBorderColor = System.color.background.brand,
                uncheckedThumbColor = System.color.icon.white,
                uncheckedTrackColor = System.color.icon.subtle,
                uncheckedBorderColor = System.color.icon.subtle,
                disabledCheckedThumbColor = System.color.icon.white.copy(alpha = 0.7f),
                disabledCheckedTrackColor = System.color.background.brand.copy(alpha = 0.5f),
                disabledCheckedBorderColor = System.color.background.brand.copy(alpha = 0.5f),
                disabledUncheckedThumbColor = System.color.icon.white.copy(alpha = 0.7f),
                disabledUncheckedTrackColor = System.color.icon.subtle.copy(alpha = 0.5f),
                disabledUncheckedBorderColor = System.color.icon.subtle.copy(alpha = 0.5f),
            ),
    )
}
