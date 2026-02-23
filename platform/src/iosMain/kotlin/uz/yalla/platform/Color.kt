package uz.yalla.platform

import androidx.compose.ui.graphics.Color
import platform.UIKit.UIColor

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
