package uz.yalla.platform.toggle

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Android actual for [NativeSwitch].
 *
 * Delegates directly to Material3 [Switch] with no additional customization.
 */
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
    )
}
