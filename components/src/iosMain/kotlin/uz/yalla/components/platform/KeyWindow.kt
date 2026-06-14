package uz.yalla.components.platform

import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

@Suppress("UNCHECKED_CAST")
public fun findKeyWindowRootController(): UIViewController? {
    val scenes = UIApplication.sharedApplication.connectedScenes

    val activeScene = scenes.firstOrNull { scene ->
        (scene as? UIWindowScene)?.activationState == UISceneActivationStateForegroundActive
    } as? UIWindowScene

    val keyWindow = activeScene?.windows?.firstOrNull { window ->
        (window as? UIWindow)?.isKeyWindow() == true
    } as? UIWindow ?: UIApplication.sharedApplication.keyWindow

    return keyWindow?.rootViewController
}
