package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController

/**
 * iOS bridge for the shared `Toggle`. The Swift side implements this to host a native switch and
 * returns a [ToggleHandle] of imperative closures Compose drives. Colors are packed as `0xAARRGGBB`
 * [Long]s (see `Color.toArgbOrZero()`).
 */
public interface ToggleFactory {
    /** Builds the native switch and returns its [ToggleHandle]. */
    public fun create(
        initialChecked: Boolean,
        initialEnabled: Boolean,
        thumbArgb: Long,
        trackArgb: Long,
        onCheckedChange: (Boolean) -> Unit
    ): ToggleHandle
}

/**
 * Handle Compose uses to update a live native switch.
 *
 * @property viewController the hosting native controller.
 * @property setChecked updates the checked state.
 * @property setEnabled updates the enabled state.
 * @property setColors updates thumb and track colors (packed `0xAARRGGBB`).
 */
public class ToggleHandle(
    public val viewController: UIViewController,
    public val setChecked: (Boolean) -> Unit,
    public val setEnabled: (Boolean) -> Unit,
    public val setColors: (thumbArgb: Long, trackArgb: Long) -> Unit
)
