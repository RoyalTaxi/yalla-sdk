package uz.yalla.platform.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-native toggle switch.
 *
 * The component catalog compiles this expect declaration for wasmJs and provides
 * a Material implementation that mirrors the Android actual.
 */
@Composable
expect fun NativeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
