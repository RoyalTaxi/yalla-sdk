package uz.yalla.components.config.primitives

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.components.primitives.button.IconButtonShape

interface IconButtonFactory {
    @Composable
    fun Content(
        icon: String,
        shape: IconButtonShape,
        iconColor: Color,
        containerColor: Color,
        borderColor: Color,
        onClick: () -> Unit,
        modifier: Modifier
    )
}
