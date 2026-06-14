package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable
import uz.yalla.design.image.ThemedImage

@Composable
public expect fun ConfirmationSheet(
    isVisible: Boolean,
    image: ThemedImage,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean = true,
    header: String? = null
)
