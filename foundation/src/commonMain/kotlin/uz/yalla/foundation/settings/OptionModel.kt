package uz.yalla.foundation.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/**
 * Presentation model for selectable option items.
 *
 * @param T Type of the underlying data item
 * @property item The data item
 * @property title Display title
 * @property icon Item icon painter
 * @property iconColor Icon tint color
 * @since 0.0.1
 */
data class OptionModel<T>(
    val item: T,
    val title: String,
    val icon: Painter? = null,
    val iconColor: Color = Color.Unspecified
)
