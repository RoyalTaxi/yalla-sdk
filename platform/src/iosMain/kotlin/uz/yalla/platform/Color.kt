package uz.yalla.platform

import androidx.compose.ui.graphics.Color
import platform.UIKit.UIColor

/**
 * Converts a Compose [Color] to a UIKit [UIColor].
 *
 * Returns `null` for [Color.Unspecified], allowing callers to fall back to a platform
 * default when no explicit color is provided.
 *
 * @return The equivalent [UIColor], or `null` if [Color.Unspecified].
 * @since 0.0.1
 */
fun Color.toUIColor(): UIColor? {
    return if (this == Color.Unspecified) {
        null
    } else {
        return UIColor(
            red = red.toDouble(),
            green = green.toDouble(),
            blue = blue.toDouble(),
            alpha = alpha.toDouble()
        )
    }
}
