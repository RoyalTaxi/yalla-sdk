package uz.yalla.components.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uz.yalla.components.config.requireConfig

@Composable
public actual fun ContentSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    title: String?,
    onClose: (() -> Unit)?,
    fullHeight: Boolean,
    sheetSwipeEnabled: Boolean,
    onFullyExpanded: (() -> Unit)?,
    content: @Composable (padding: PaddingValues) -> Unit
) {
    requireConfig().sheet.ContentContent(
        isVisible = isVisible,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        onClose = onClose,
        fullHeight = fullHeight,
        sheetSwipeEnabled = sheetSwipeEnabled,
        onFullyExpanded = onFullyExpanded,
        content = content
    )
}
