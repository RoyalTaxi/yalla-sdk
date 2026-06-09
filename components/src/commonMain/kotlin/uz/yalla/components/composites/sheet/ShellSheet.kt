package uz.yalla.components.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ShellSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    fullHeight: Boolean = false,
    sheetSwipeEnabled: Boolean = true,
    content: @Composable (padding: PaddingValues) -> Unit
)
