package uz.yalla.components.primitives.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.components.config.requireConfig

@Composable
public actual fun LoadingIndicator(
    color: Color,
    modifier: Modifier
) {
    requireConfig().loadingIndicator.Content(
        color = color,
        modifier = modifier
    )
}
