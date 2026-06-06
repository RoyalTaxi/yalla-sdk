package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.components.composites.item.SelectableItemModel

@Composable
expect fun SelectionSheet(
    isVisible: Boolean,
    title: String,
    items: List<SelectableItemModel>,
    selectedId: String?,
    onSelect: (id: String) -> Unit,
    onDismissRequest: () -> Unit
)
