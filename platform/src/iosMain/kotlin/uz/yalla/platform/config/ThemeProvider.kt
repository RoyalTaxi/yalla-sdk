package uz.yalla.platform.config

import androidx.compose.runtime.Composable

/**
 * Provides Yalla theme context for native sheets presented outside the Compose tree.
 * @since 0.0.1
 */
interface ThemeProvider {
    @Composable
    fun provide(content: @Composable () -> Unit)
}
