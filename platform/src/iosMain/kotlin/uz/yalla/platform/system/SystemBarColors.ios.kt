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

@Composable
actual fun SystemBarColors(
    statusBarColor: Color,
    navigationBarColor: Color
) {
    val useDarkIcons = statusBarColor.luminance() > 0.5f
    SystemBarColors(darkIcons = useDarkIcons)
}

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
