package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController

public interface LoadingIndicatorFactory {
    public fun create(color: Long): LoadingIndicatorHandle
}

public class LoadingIndicatorHandle(
    public val viewController: UIViewController,
    public val setColor: (Long) -> Unit
)
