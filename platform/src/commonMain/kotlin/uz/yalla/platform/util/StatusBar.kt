package uz.yalla.platform.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Returns the height of the platform status bar as a [Dp] value.
 *
 * On Android, queries `WindowInsets.statusBars` padding.
 * On iOS, reads `UIApplication.sharedApplication.statusBarFrame.size.height`.
 *
 * Prefer [ToolbarState.contentPadding][uz.yalla.platform.navigation.ToolbarState.contentPadding]
 * for screens inside [NativeNavHost][uz.yalla.platform.navigation.NativeNavHost], as it accounts
 * for both the status bar and the navigation bar. Use this function only when you need the raw
 * status bar height outside of the navigation host (e.g., floating overlays on a map).
 *
 * @return The status bar height in density-independent pixels.
 * @see uz.yalla.platform.navigation.ToolbarState.contentPadding
 * @since 0.0.1
 */
@Composable
expect fun statusBarHeight(): Dp
