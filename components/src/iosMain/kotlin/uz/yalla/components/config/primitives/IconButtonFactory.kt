package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController
import uz.yalla.components.primitives.button.IconButtonShape

public interface IconButtonFactory {
    public fun create(
        icon: String,
        shape: IconButtonShape,
        iconArgb: Long,
        containerArgb: Long,
        borderArgb: Long,
        onClick: () -> Unit
    ): IconButtonHandle
}

public const val ICON_BUTTON_COLOR_UNSET: Long = Long.MIN_VALUE

public class IconButtonHandle(
    public val viewController: UIViewController,
    public val setIcon: (String) -> Unit,
    public val setColors: (iconArgb: Long, containerArgb: Long, borderArgb: Long) -> Unit
)
