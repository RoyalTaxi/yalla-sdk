package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.components.composites.item.ActionableItemModel
import uz.yalla.components.config.requireConfig

@Composable
public actual fun ActionSheet(
    isVisible: Boolean,
    title: String,
    items: List<ActionableItemModel>,
    onAction: (id: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    requireConfig().sheet.ActionContent(
        isVisible = isVisible,
        title = title,
        items = items,
        onAction = onAction,
        onDismissRequest = onDismissRequest
    )
}
