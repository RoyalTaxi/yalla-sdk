package uz.yalla.platform.util

import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

/**
 * Finds the topmost presented UIViewController from the key window.
 * Traverses the presentation chain to find the leaf controller.
 * @since 0.0.1
 */
internal fun findRootViewController(): UIViewController? {
    val scenes = UIApplication.sharedApplication.connectedScenes
    val windowScene = scenes.firstOrNull { it is UIWindowScene } as? UIWindowScene
    val window = windowScene?.windows?.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
    var controller = window?.rootViewController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}
