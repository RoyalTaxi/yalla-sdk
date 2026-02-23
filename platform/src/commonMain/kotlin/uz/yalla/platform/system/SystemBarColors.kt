package uz.yalla.platform.system

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun SystemBarColors(
    statusBarColor: Color,
    navigationBarColor: Color = statusBarColor
)

@Composable
expect fun SystemBarColors(darkIcons: Boolean)
