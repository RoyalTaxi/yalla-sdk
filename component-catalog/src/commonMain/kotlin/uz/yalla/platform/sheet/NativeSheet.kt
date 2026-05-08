package uz.yalla.platform.sheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * Platform-native bottom sheet.
 *
 * The component catalog compiles this expect declaration for wasmJs and provides
 * a Material implementation that mirrors the Android actual.
 */
@Composable
expect fun NativeSheet(
    isVisible: Boolean,
    shape: Shape,
    containerColor: Color,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean = true,
    onDismissAttempt: () -> Unit = {},
    isDark: Boolean? = null,
    skipPartiallyExpanded: Boolean = false,
    onFullyExpanded: (() -> Unit)? = null,
    content: @Composable () -> Unit,
)
