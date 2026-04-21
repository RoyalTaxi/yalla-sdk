package uz.yalla.media.utils

import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

/**
 * Resolves the topmost [UIViewController] that is safe to present from.
 *
 * The lookup strategy:
 * 1. Finds the key window via the modern scene-based API ([UIWindowScene]).
 * 2. Falls back to the deprecated [UIApplication.keyWindow] for older iOS versions.
 * 3. Walks the `presentedViewController` chain to reach the topmost controller.
 * 4. Validates that the returned controller's view is still in a window and not
 *    mid-dismissal.
 *
 * @return The topmost presentable [UIViewController], or `null` if none is available.
 * @since 0.0.1
 */
internal fun getRootViewController(): UIViewController? {
    // UIApplication.keyWindow is deprecated in iOS 13 but retained as a fallback when no UIWindowScene is active.
    @Suppress("DEPRECATION")
    val legacyKeyWindow = UIApplication.sharedApplication.keyWindow

    val keyWindow =
        UIApplication.sharedApplication.connectedScenes
            .filterIsInstance<UIWindowScene>()
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
            ?.windows
            ?.filterIsInstance<UIWindow>()
            ?.firstOrNull { it.isKeyWindow() }
            ?: legacyKeyWindow

    var rootVC = keyWindow?.rootViewController()

    while (true) {
        val presented = rootVC?.presentedViewController() ?: break
        if (presented.view?.window != null && !presented.isBeingDismissed()) {
            rootVC = presented
        } else {
            break
        }
    }

    return rootVC?.takeIf { it.view?.window != null && !it.isBeingDismissed() }
        ?: keyWindow?.rootViewController()
}
