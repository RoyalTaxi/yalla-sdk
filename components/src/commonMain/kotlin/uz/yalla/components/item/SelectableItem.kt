package uz.yalla.components.item

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Immutable
data class SelectableItemColors(
    val iconColor: Color,
    val textColor: Color,
    val containerColor: Color,
    val selectedIconColor: Color,
    val selectedTextColor: Color,
    val selectedContainerColor: Color
) {
    @Composable
    fun containerColorFor(selected: Boolean) =
        if (selected) selectedContainerColor else containerColor
}

@Composable
fun SelectableItem(
    selected: Boolean = false,
    painter: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(

    ) {

    }
}