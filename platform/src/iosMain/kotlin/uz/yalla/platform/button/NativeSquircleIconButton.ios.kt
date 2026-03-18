package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.yalla.platform.config.requireIosConfig
import uz.yalla.platform.model.IconType

private val SquircleShape = RoundedCornerShape(12.dp)

@Composable
actual fun NativeSquircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier,
    border: BorderStroke?,
    background: Color
) {
    val config = requireIosConfig()

    NativeIconButton(
        iconType = iconType,
        onClick = onClick,
        modifier = modifier,
        border = border,
        background = background,
        backgroundShape = SquircleShape,
        factory = { icon, click, borderWidth, borderColor ->
            config.squircleButton.create(icon, click, borderWidth, borderColor)
        },
    )
}
