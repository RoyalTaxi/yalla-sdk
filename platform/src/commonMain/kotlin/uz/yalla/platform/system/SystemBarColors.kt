package uz.yalla.platform.system

import androidx.compose.runtime.Composable

/**
 * Controls system bar icon contrast (light vs dark icons).
 *
 * `YallaTheme` owns status bar and navigation bar background colors; consumers should not
 * set raw colors through this function. Use this overload to toggle icon style only
 * (e.g., switching to light icons over a full-bleed map, or dark icons over a white sheet).
 *
 * On Android: uses `WindowCompat.getInsetsController` to set `isAppearanceLightStatusBars`
 * and `isAppearanceLightNavigationBars`. No-ops if the context is not an `Activity`.
 * On iOS: uses `UIApplication.setStatusBarStyle` (deprecated API; the per-ViewController
 * alternative requires a Swift-side UIViewController subclass not feasible from Kotlin/Compose).
 *
 * @param darkIcons `true` for dark icons on a light background, `false` for light icons on dark.
 * @since 0.0.1
 */
@Composable
expect fun SystemBarColors(darkIcons: Boolean)
