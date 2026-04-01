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

/**
 * iOS actual for [rememberInAppBrowser].
 *
 * Opens URLs in an [SFSafariViewController] presented as a page sheet from the key window's
 * root view controller. Invalid URLs (where [NSURL.URLWithString] returns `null`) are
 * silently ignored.
 */
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

/**
 * Finds the key window's root [UIViewController] for modal presentation.
 *
 * @return The root view controller of the active key window, or `null` if unavailable.
 */
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
