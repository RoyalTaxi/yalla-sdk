package uz.yalla.components.primitives.button

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = System.color.text.subtle,
    containerColor: Color = System.color.background.secondary
) {
    Surface(
        color = containerColor,
        shape = CircleShape,
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = contentColor,
            style = System.font.body.caption,
            modifier = Modifier.padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
        )
    }
}
