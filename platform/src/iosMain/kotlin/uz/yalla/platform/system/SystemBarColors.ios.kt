@file:Suppress("DEPRECATION")

package uz.yalla.platform.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

/**
 * iOS actual for [SystemBarColors] (color overload).
 *
 * Derives the icon style from the [statusBarColor] luminance and delegates to the
 * `darkIcons` overload. The [navigationBarColor] is ignored on iOS because the system
 * navigation bar (home indicator area) does not have a configurable background.
 */
@Composable
actual fun SystemBarColors(
    statusBarColor: Color,
    navigationBarColor: Color
) {
    val useDarkIcons = statusBarColor.luminance() > 0.5f
    SystemBarColors(darkIcons = useDarkIcons)
}

/**
 * iOS actual for [SystemBarColors] (icon-style overload).
 *
 * Uses the deprecated `UIApplication.setStatusBarStyle` API. While deprecated since iOS 9,
 * the per-ViewController `preferredStatusBarStyle` approach requires a custom Swift
 * `UIViewController` subclass, which is not feasible from the Kotlin/Compose side.
 *
 * Keyed on [darkIcons] via [LaunchedEffect] to avoid re-running on every recomposition
 * (important during 60fps animations).
 */
@Composable
actual fun SystemBarColors(darkIcons: Boolean) {
    // LaunchedEffect keyed on darkIcons — only re-runs when the value actually
    // changes, unlike SideEffect which fires on every recomposition (60fps
    // during animations).
    // Note: setStatusBarStyle is deprecated since iOS 9 in favour of
    // per-ViewController preferredStatusBarStyle, but the VC-based approach
    // requires a custom UIViewController subclass (Swift-side concern).
    LaunchedEffect(darkIcons) {
        val style = if (darkIcons) {
            UIStatusBarStyleDarkContent
        } else {
            UIStatusBarStyleLightContent
        }
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
    }
}
