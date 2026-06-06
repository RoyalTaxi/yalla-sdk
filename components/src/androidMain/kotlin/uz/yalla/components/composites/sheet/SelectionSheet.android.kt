package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.components.composites.item.SelectableItemModel
import uz.yalla.components.config.requireConfig

@Composable
actual fun SelectionSheet(
    isVisible: Boolean,
    title: String,
    items: List<SelectableItemModel>,
    selectedId: String?,
    onSelect: (id: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    requireConfig().sheet.SelectionContent(
        isVisible = isVisible,
        title = title,
        items = items,
        selectedId = selectedId,
        onSelect = onSelect,
        onDismissRequest = onDismissRequest
    )
}
