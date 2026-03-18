package uz.yalla.platform.config

import platform.UIKit.UIViewController

/**
 * Factory for creating squircle-shaped (rounded rect) native iOS icon buttons.
 * @since 0.0.1
 */
interface SquircleIconButtonFactory {
    fun create(
        icon: String,
        onClick: () -> Unit,
        borderWidth: Double,
        borderColor: Long,
    ): UIViewController
}
