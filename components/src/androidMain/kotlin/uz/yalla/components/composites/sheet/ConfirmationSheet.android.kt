package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.components.config.requireConfig

@Composable
actual fun ConfirmationSheet(
    isVisible: Boolean,
    imageResource: String,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean
) {
    requireConfig().sheet.ConfirmationContent(
        isVisible = isVisible,
        imageResource = imageResource,
        title = title,
        description = description,
        actionText = actionText,
        onAction = onAction,
        onDismissRequest = onDismissRequest,
        dismissEnabled = dismissEnabled
    )
}
