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
        }
    )
}

private fun Color.toUIColorOrNull(): UIColor? =
    takeIf { it.isSpecified }?.run {
        UIColor(red = red.toDouble(), green = green.toDouble(), blue = blue.toDouble(), alpha = alpha.toDouble())
    }
