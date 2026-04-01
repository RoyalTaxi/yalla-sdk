package uz.yalla.maps.compose

import androidx.compose.ui.graphics.Color
import platform.UIKit.UIColor

/**
 * Converts this Compose [Color] to a UIKit [UIColor].
 *
 * Maps the red, green, blue, and alpha components directly.
 *
 * @return A [UIColor] with matching RGBA values.
 * @since 0.0.1
 */
internal fun Color.toUIColor(): UIColor =
    UIColor(
        red = red.toDouble(),
        green = green.toDouble(),
        blue = blue.toDouble(),
        alpha = alpha.toDouble()
    )
