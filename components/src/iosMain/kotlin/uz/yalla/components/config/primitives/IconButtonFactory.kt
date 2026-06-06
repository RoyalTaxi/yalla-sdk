package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController
import uz.yalla.components.primitives.button.IconButtonShape

interface IconButtonFactory {
    fun create(
        icon: String,
        shape: IconButtonShape,
        iconArgb: Long,
        containerArgb: Long,
        onClick: () -> Unit
    ): IconButtonHandle
}

class IconButtonHandle(
    val viewController: UIViewController,
    val setIcon: (String) -> Unit,
    val setColors: (iconArgb: Long, containerArgb: Long) -> Unit
)
