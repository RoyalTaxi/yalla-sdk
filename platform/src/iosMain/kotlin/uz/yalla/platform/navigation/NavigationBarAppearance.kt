package uz.yalla.platform.navigation

/**
 * Global appearance configuration for the iOS `UINavigationBar`.
 *
 * Applied once when [NativeNavHost] creates its `UINavigationController`. All color values
 * use ARGB [Long] encoding (e.g., `0xFF_1A1A1A` for an opaque dark color). A value of `0L`
 * means "use the system default" for that property.
 *
 * Pass an instance via [IosPlatformConfig.Builder.navigationBarAppearance][uz.yalla.platform.config.IosPlatformConfig.Builder].
 *
 * @param tintColor Tint color for bar button items (back arrow, action icons). `0L` for system default.
 * @param titleColor Color for both the standard title and large title text. `0L` for system default.
 * @param backgroundColor Background color of the navigation bar. `0L` for system default.
 * @param isTranslucent Whether the bar should be translucent. Default `true`.
 * @param showsSeparator Whether to show the bottom separator (shadow). Default `true`.
 *   Set to `false` to remove the hairline divider.
 * @param largeTitleFontName PostScript name of the font for large titles (e.g., `"SFProDisplay-Bold"`).
 *   `null` uses the system font at 34 pt.
 * @param titleFontName PostScript name of the font for standard titles.
 *   `null` uses the system font at 17 pt.
 * @see NativeNavHost
 * @since 0.0.5
 */
data class NavigationBarAppearance(
    val tintColor: Long = 0,
    val titleColor: Long = 0,
    val backgroundColor: Long = 0,
    val isTranslucent: Boolean = true,
    val showsSeparator: Boolean = true,
    val largeTitleFontName: String? = null,
    val titleFontName: String? = null,
)
