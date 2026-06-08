package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import uz.yalla.components.composites.item.ActionableItemModel
import uz.yalla.components.config.requireConfig
import uz.yalla.components.platform.findKeyWindowRootController

@Composable
actual fun ActionSheet(
    isVisible: Boolean,
    title: String,
    items: List<ActionableItemModel>,
    onAction: (id: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val currentOnAction by rememberUpdatedState(onAction)
    val currentOnDismissRequest by rememberUpdatedState(onDismissRequest)

    DisposableEffect(isVisible) {
        if (!isVisible) {
            return@DisposableEffect onDispose {}
        }

        val parent = findKeyWindowRootController() ?: return@DisposableEffect onDispose {}
        val handle = requireConfig().sheet.createAction(
            title = title,
            items = items,
            onAction = { currentOnAction(it) },
            onDismissRequest = { currentOnDismissRequest() }
        )
        handle.present(parent)

        onDispose { handle.dismiss() }
    }
}
