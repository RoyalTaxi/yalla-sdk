package uz.yalla.platform.indicator

import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified

@Composable
actual fun NativeLoadingIndicator(
    modifier: Modifier,
    color: Color,
    backgroundColor: Color
) {
    val finalModifier = modifier.applyIf(backgroundColor.isSpecified) { background(backgroundColor) }

    if (color.isSpecified) {
        CircularProgressIndicator(modifier = finalModifier, color = color)
    } else {
        CircularProgressIndicator(modifier = finalModifier)
    }
}

private inline fun Modifier.applyIf(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condition) block() else this
