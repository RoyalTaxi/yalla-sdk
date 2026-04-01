package uz.yalla.platform.indicator

import androidx.compose.foundation.background
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified

/**
 * Android actual for [NativeLoadingIndicator].
 *
 * Renders a Material3 [CircularProgressIndicator]. When [color] is specified,
 * it is passed as the indicator's foreground color; otherwise the theme default is used.
 * The [backgroundColor] is applied via [Modifier.background] only when specified.
 */
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

/**
 * Conditionally applies a [Modifier] transformation.
 *
 * @param condition When `true`, [block] is invoked on this modifier; otherwise returns `this`.
 * @param block Modifier transformation to apply conditionally.
 * @return The original or transformed [Modifier].
 */
private inline fun Modifier.applyIf(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier = if (condition) block() else this
