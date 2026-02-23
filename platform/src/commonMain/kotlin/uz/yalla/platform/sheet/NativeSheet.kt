package uz.yalla.platform.sheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

@Composable
expect fun NativeSheet(
    isVisible: Boolean,
    shape: Shape,
    containerColor: Color,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean = true,
    onDismissAttempt: () -> Unit = {},
    isDark: Boolean? = null,
    onFullyExpanded: (() -> Unit)? = null,
    content: @Composable () -> Unit
)
