package uz.yalla.components.composites.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Composable
public fun ServiceHeader(
    painter: Painter,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.padding(8.dp).height(32.dp)
        )

        Text(
            text = label,
            color = System.color.text.base,
            style = System.font.title.base,
            modifier = Modifier.padding(vertical = 8.dp).padding(end = 16.dp)
        )
    }
}
