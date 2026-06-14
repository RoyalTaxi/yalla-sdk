package uz.yalla.components.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.components.config.requireConfig

@Composable
public actual fun ShellSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    fullHeight: Boolean,
    sheetSwipeEnabled: Boolean,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    requireConfig().sheet.ShellContent(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        fullHeight = fullHeight,
        sheetSwipeEnabled = sheetSwipeEnabled,
        content = content
    )
}
