package uz.yalla.platform.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Sets the status bar and navigation bar background colors.
 *
 * On Android, applies the colors to the system window decor.
 * On iOS, configures the `UINavigationBar` and status bar appearance.
 *
 * @param statusBarColor Color applied to the status bar area.
 * @param navigationBarColor Color applied to the system navigation bar. Defaults to [statusBarColor].
 * @since 0.0.1
 */
@Composable
expect fun SystemBarColors(
    statusBarColor: Color,
    navigationBarColor: Color = statusBarColor
)

/**
 * Controls system bar icon contrast (light vs dark icons).
 *
 * Use this overload when you only need to toggle icon style without specifying
 * explicit bar colors (e.g., transparent bars over a map).
 *
 * @param darkIcons `true` for dark icons on a light background, `false` for light icons on dark.
 * @since 0.0.1
 */
@Composable
expect fun SystemBarColors(darkIcons: Boolean)
