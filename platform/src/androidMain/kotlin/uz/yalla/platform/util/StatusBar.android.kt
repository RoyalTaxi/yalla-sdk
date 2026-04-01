package uz.yalla.platform.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * Android actual for [statusBarHeight].
 *
 * Reads the top padding from [WindowInsets.statusBars] via [asPaddingValues].
 */
@Composable
actual fun statusBarHeight(): Dp =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
