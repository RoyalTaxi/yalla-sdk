package uz.yalla.platform.config

import androidx.compose.runtime.Composable

/**
 * Provides Yalla theme context for native sheets presented outside the main Compose tree.
 *
 * When a [NativeSheet][uz.yalla.platform.sheet.NativeSheet] is presented on iOS, it creates
 * a new [ComposeUIViewController][androidx.compose.ui.window.ComposeUIViewController] that lives
 * outside the app's main Compose hierarchy. Without a [ThemeProvider], the sheet content would
 * not have access to the design system's `CompositionLocal` values (colors, typography, etc.).
 *
 * Implement this interface to wrap sheet content in your app's theme:
 *
 * ```kotlin
 * class YallaThemeProvider : ThemeProvider {
 *     @Composable
 *     override fun provide(content: @Composable () -> Unit) {
 *         YallaTheme { content() }
 *     }
 * }
 * ```
 *
 * Register via [IosPlatformConfig.Builder.themeProvider]. Optional on Android (sheets share
 * the Compose tree).
 *
 * @see IosPlatformConfig
 * @since 0.0.1
 */
interface ThemeProvider {
    /**
     * Wraps the given [content] with the app's Compose theme.
     *
     * @param content The sheet content composable that needs theme context.
     */
    @Composable
    fun provide(content: @Composable () -> Unit)
}
