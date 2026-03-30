package uz.yalla.platform.browser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationPageSheet
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene

@Composable
actual fun rememberInAppBrowser(): InAppBrowserLauncher {
    return remember {
        object : InAppBrowserLauncher {
            override fun open(url: String) {
                val nsUrl = NSURL.URLWithString(url) ?: return
                val safari = SFSafariViewController(uRL = nsUrl, entersReaderIfAvailable = false)
                safari.setModalPresentationStyle(UIModalPresentationPageSheet)
                findRootController()?.presentViewController(safari, animated = true, completion = null)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
private fun findRootController(): UIViewController? {
    val scenes = UIApplication.sharedApplication.connectedScenes as? Set<*>

    val activeScene =
        scenes?.firstOrNull { scene ->
            (scene as? UIWindowScene)?.activationState == UISceneActivationStateForegroundActive
        } as? UIWindowScene

    val keyWindow =
        activeScene?.windows?.firstOrNull { window ->
            (window as? UIWindow)?.isKeyWindow() == true
        } as? UIWindow ?: UIApplication.sharedApplication.keyWindow

    return keyWindow?.rootViewController
}
