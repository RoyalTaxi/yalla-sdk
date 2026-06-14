package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController

public interface ToggleFactory {
    public fun create(
        initialChecked: Boolean,
        initialEnabled: Boolean,
        thumbArgb: Long,
        trackArgb: Long,
        onCheckedChange: (Boolean) -> Unit
    ): ToggleHandle
}

public class ToggleHandle(
    public val viewController: UIViewController,
    public val setChecked: (Boolean) -> Unit,
    public val setEnabled: (Boolean) -> Unit,
    public val setColors: (thumbArgb: Long, trackArgb: Long) -> Unit
)
