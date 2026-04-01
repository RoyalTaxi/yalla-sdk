package uz.yalla.platform.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityIndicatorView
import platform.UIKit.UIActivityIndicatorViewStyleMedium
import platform.UIKit.UIColor

/**
 * iOS actual for [NativeLoadingIndicator].
 *
 * Renders a native [UIActivityIndicatorView] with medium style via [UIKitView].
 * The spinner starts animating immediately on creation. The [color] and [backgroundColor]
 * are applied both in `factory` (initial) and `update` (recomposition) blocks.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeLoadingIndicator(
    modifier: Modifier,
    color: Color,
    backgroundColor: Color
) {
    UIKitView(
        modifier = modifier,
        factory = {
            UIActivityIndicatorView(UIActivityIndicatorViewStyleMedium).apply {
                color.toUIColorOrNull()?.let { this.color = it }
                this.backgroundColor = backgroundColor.toUIColorOrNull() ?: UIColor.clearColor
                setOpaque(backgroundColor.isSpecified && backgroundColor.alpha == 1f)
                startAnimating()
            }
        },
        update = { indicator ->
            color.toUIColorOrNull()?.let { indicator.color = it }
            indicator.backgroundColor = backgroundColor.toUIColorOrNull() ?: UIColor.clearColor
            indicator.setOpaque(backgroundColor.isSpecified && backgroundColor.alpha == 1f)
        }
    )
}

/**
 * Converts a Compose [Color] to [UIColor], returning `null` for [Color.Unspecified].
 */
private fun Color.toUIColorOrNull(): UIColor? =
    takeIf { it.isSpecified }?.run {
        UIColor(red = red.toDouble(), green = green.toDouble(), blue = blue.toDouble(), alpha = alpha.toDouble())
    }
