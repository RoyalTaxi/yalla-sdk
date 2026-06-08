package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.components.config.requireConfig
import uz.yalla.design.image.ThemedImage

@Composable
actual fun ConfirmationSheet(
    isVisible: Boolean,
    image: ThemedImage,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean
) {
    requireConfig().sheet.ConfirmationContent(
        isVisible = isVisible,
        image = image,
        title = title,
        description = description,
        actionText = actionText,
        onAction = onAction,
        onDismissRequest = onDismissRequest,
        dismissEnabled = dismissEnabled
    )
}
