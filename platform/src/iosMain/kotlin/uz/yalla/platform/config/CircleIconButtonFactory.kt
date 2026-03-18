package uz.yalla.platform.config

import platform.UIKit.UIViewController

/**
 * Factory for creating circle-shaped native iOS icon buttons.
 * @since 0.0.1
 */
interface CircleIconButtonFactory {
    fun create(
        icon: String,
        onClick: () -> Unit,
        borderWidth: Double,
        borderColor: Long,
    ): UIViewController
}
