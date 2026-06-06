package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController

interface ToggleFactory {
    fun create(
        initialChecked: Boolean,
        initialEnabled: Boolean,
        thumbArgb: Long,
        trackArgb: Long,
        onCheckedChange: (Boolean) -> Unit
    ): ToggleHandle
}

class ToggleHandle(
    val viewController: UIViewController,
    val setChecked: (Boolean) -> Unit,
    val setEnabled: (Boolean) -> Unit,
    val setColors: (thumbArgb: Long, trackArgb: Long) -> Unit
)
