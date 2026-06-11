package uz.yalla.components.composites.sheet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ContentSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    onClose: (() -> Unit)? = null,
    fullHeight: Boolean = false,
    sheetSwipeEnabled: Boolean = true,
    onFullyExpanded: (() -> Unit)? = null,
    content: @Composable (padding: PaddingValues) -> Unit
)
