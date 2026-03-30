package uz.yalla.platform.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
actual fun statusBarHeight(): Dp =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
