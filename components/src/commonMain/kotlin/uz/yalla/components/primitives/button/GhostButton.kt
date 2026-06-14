package uz.yalla.components.primitives.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme

private const val DisabledAlpha = 0.4f

@Composable
public fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = System.color.text.subtle,
    style: TextStyle = System.font.body.small.regular
) {
    Text(
        text = text,
        style = style,
        color = if (enabled) color else color.copy(alpha = DisabledAlpha),
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                ).padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Preview
@Composable
private fun Preview() =
    YallaTheme {
        GhostButton(text = "Resend code", onClick = {})
    }
