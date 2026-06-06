package uz.yalla.components.composites.sheet

import androidx.compose.runtime.Composable

@Composable
expect fun ConfirmationSheet(
    isVisible: Boolean,
    imageResource: String,
    title: String,
    description: String,
    actionText: String,
    onAction: () -> Unit,
    onDismissRequest: () -> Unit,
    dismissEnabled: Boolean = true
)
