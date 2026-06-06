package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController

interface LoadingIndicatorFactory {
    fun create(color: Long): LoadingIndicatorHandle
}

class LoadingIndicatorHandle(
    val viewController: UIViewController,
    val setColor: (Long) -> Unit
)
