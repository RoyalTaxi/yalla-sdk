@file:Suppress("DEPRECATION")

package uz.yalla.foundation.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

@Composable
public actual fun SystemBarColors(darkIcons: Boolean) {
    LaunchedEffect(darkIcons) {
        val style = if (darkIcons) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
    }
}
