// UIApplication.setStatusBarStyle is deprecated since iOS 9, but the per-ViewController replacement requires a
// Swift-side UIViewController subclass that is not feasible from Kotlin/Compose — see the function KDoc for details.
@file:Suppress("DEPRECATION")

package uz.yalla.platform.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

/**
 * iOS actual for [SystemBarColors].
 *
 * Uses the deprecated `UIApplication.setStatusBarStyle` API. While deprecated since iOS 9,
 * the per-ViewController `preferredStatusBarStyle` approach requires a custom Swift
 * `UIViewController` subclass, which is not feasible from the Kotlin/Compose side.
 *
 * Keyed on [darkIcons] via [LaunchedEffect] to avoid re-running on every recomposition
 * during 60fps animations.
 */
@Composable
actual fun SystemBarColors(darkIcons: Boolean) {
    LaunchedEffect(darkIcons) {
        val style = if (darkIcons) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
    }
}
