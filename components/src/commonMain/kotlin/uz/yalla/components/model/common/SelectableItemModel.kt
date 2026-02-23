package uz.yalla.components.model.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/**
 * Model for selectable list items.
 *
 * @param T Type of the underlying data item
 * @property item The data item
 * @property title Display title
 * @property icon Item icon painter
 * @property iconColor Icon tint color
 */
data class SelectableItemModel<T>(
    val item: T,
    val title: String,
    val icon: Painter,
    val iconColor: Color
)
