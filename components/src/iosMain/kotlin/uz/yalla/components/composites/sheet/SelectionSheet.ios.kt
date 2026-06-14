package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import uz.yalla.components.composites.item.SelectableItemModel
import uz.yalla.components.config.requireConfig
import uz.yalla.components.platform.findKeyWindowRootController

@Composable
public actual fun SelectionSheet(
    isVisible: Boolean,
    title: String,
    items: List<SelectableItemModel>,
    selectedId: String?,
    onSelect: (id: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    DisposableEffect(isVisible) {
        if (!isVisible) {
            return@DisposableEffect onDispose {}
        }

        val parent = findKeyWindowRootController() ?: return@DisposableEffect onDispose {}
        val handle = requireConfig().sheet.createSelection(
            title = title,
            items = items,
            selectedId = selectedId,
            onSelect = { currentOnSelect(it) },
            onDismissRequest = { currentOnDismissRequest() }
        )
        handle.present(parent)

        onDispose { handle.dismiss() }
    }
}
