package uz.yalla.platform.system

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

/**
 * Android actual for [SystemBarColors].
 *
 * Sets both status bar and navigation bar icon appearance to dark or light via
 * [WindowCompat.getInsetsController]. No-ops if the context is not an [Activity].
 */
@Composable
actual fun SystemBarColors(darkIcons: Boolean) {
    val context = LocalContext.current

    // LaunchedEffect keyed on darkIcons — only re-runs when the value actually
    // changes, matching the iOS actual's behavior and avoiding redundant calls
    // on every recomposition during 60fps animations.
    LaunchedEffect(darkIcons) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        val view = window.decorView
        val insetsController = WindowCompat.getInsetsController(window, view)

        insetsController.isAppearanceLightStatusBars = darkIcons
        insetsController.isAppearanceLightNavigationBars = darkIcons
    }
}
