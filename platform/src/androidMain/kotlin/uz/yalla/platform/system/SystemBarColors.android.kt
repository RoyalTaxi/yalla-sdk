package uz.yalla.platform.system

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

/**
 * Android actual for [SystemBarColors] (color overload).
 *
 * Uses [WindowCompat.getInsetsController] to toggle light/dark status bar and
 * navigation bar icons based on the luminance of the provided colors.
 * A luminance > 0.5 results in dark icons (light background); otherwise light icons.
 *
 * Requires the hosting [Activity] window; silently no-ops if the context is not an Activity.
 */
@Composable
actual fun SystemBarColors(
    statusBarColor: Color,
    navigationBarColor: Color
) {
    val useDarkStatusBarIcons = statusBarColor.luminance() > 0.5f
    val useDarkNavigationBarIcons = navigationBarColor.luminance() > 0.5f
    val context = LocalContext.current

    // LaunchedEffect keyed on the derived booleans — only re-runs when icon style
    // actually changes, unlike SideEffect which fires on every recomposition (60fps
    // during animations).
    LaunchedEffect(useDarkStatusBarIcons, useDarkNavigationBarIcons) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        val view = window.decorView
        val insetsController = WindowCompat.getInsetsController(window, view)

        insetsController.isAppearanceLightStatusBars = useDarkStatusBarIcons
        insetsController.isAppearanceLightNavigationBars = useDarkNavigationBarIcons
    }
}

/**
 * Android actual for [SystemBarColors] (icon-style overload).
 *
 * Sets both status bar and navigation bar icon appearance to dark or light.
 * Uses [WindowCompat.getInsetsController]; no-ops if the context is not an [Activity].
 */
@Composable
actual fun SystemBarColors(darkIcons: Boolean) {
    val context = LocalContext.current

    // LaunchedEffect keyed on darkIcons — only re-runs when the value actually
    // changes, matching the iOS actual's behavior.
    LaunchedEffect(darkIcons) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        val view = window.decorView
        val insetsController = WindowCompat.getInsetsController(window, view)

        insetsController.isAppearanceLightStatusBars = darkIcons
        insetsController.isAppearanceLightNavigationBars = darkIcons
    }
}
