package uz.yalla.foundation.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

data class OptionModel<T>(
    val item: T,
    val title: String,
    val icon: Painter? = null,
    val iconColor: Color = Color.Unspecified
)
