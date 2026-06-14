package uz.yalla.components.config.primitives

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

public interface LoadingIndicatorFactory {
    @Composable
    public fun Content(
        color: Color,
        modifier: Modifier
    )
}
