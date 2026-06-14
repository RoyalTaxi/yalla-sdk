package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.components.composites.item.ActionableItemModel

@Composable
public expect fun ActionSheet(
    isVisible: Boolean,
    title: String,
    items: List<ActionableItemModel>,
    onAction: (id: String) -> Unit,
    onDismissRequest: () -> Unit
)
