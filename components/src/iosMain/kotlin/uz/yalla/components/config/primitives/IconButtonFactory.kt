package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController
import uz.yalla.components.primitives.button.IconButtonShape

/**
 * iOS bridge for the shared `IconButton`. The Swift side implements this to host a native icon button
 * and returns an [IconButtonHandle] of imperative closures Compose drives. Colors are packed as
 * `0xAARRGGBB` [Long]s (see `Color.toArgbOrZero()`).
 */
public interface IconButtonFactory {
    /** Builds the native icon button and returns its [IconButtonHandle]. */
    public fun create(
        icon: String,
        shape: IconButtonShape,
        iconArgb: Long,
        containerArgb: Long,
        borderArgb: Long,
        onClick: () -> Unit
    ): IconButtonHandle
}

/**
 * Handle Compose uses to update a live native icon button.
 *
 * @property viewController the hosting native controller.
 * @property setIcon swaps the rendered icon.
 * @property setColors updates icon, container, and border colors (packed `0xAARRGGBB`).
 */
public class IconButtonHandle(
    public val viewController: UIViewController,
    public val setIcon: (String) -> Unit,
    public val setColors: (iconArgb: Long, containerArgb: Long, borderArgb: Long) -> Unit
)
