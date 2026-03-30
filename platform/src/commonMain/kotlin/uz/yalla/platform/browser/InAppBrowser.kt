package uz.yalla.platform.browser

import androidx.compose.runtime.Composable

/**
 * Opens the given [url] in a platform-native in-app browser.
 *
 * On Android, launches a Chrome Custom Tab. On iOS, presents an SFSafariViewController
 * as a page sheet. The user stays within the app in both cases.
 *
 * @param url The URL to open.
 * @since 0.0.6-alpha05
 */
@Composable
expect fun rememberInAppBrowser(): InAppBrowserLauncher

/**
 * Launcher for opening URLs in an in-app browser.
 * @since 0.0.6-alpha05
 */
interface InAppBrowserLauncher {
    fun open(url: String)
}
