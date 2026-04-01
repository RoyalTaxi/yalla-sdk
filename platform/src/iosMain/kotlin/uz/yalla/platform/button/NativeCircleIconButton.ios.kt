package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.platform.config.requireIosConfig
import uz.yalla.platform.model.IconType

/**
 * iOS actual for [NativeCircleIconButton].
 *
 * Delegates to [NativeIconButton] with a [CircleShape] background and the
 * [CircleIconButtonFactory] from [IosPlatformConfig]. The native button is
 * recreated via `key(iconType)` when the icon changes.
 */
@Composable
actual fun NativeCircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier,
    alpha: Float,
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
        backgroundShape = CircleShape,
        factory = { icon, click, borderWidth, borderColor ->
            config.circleButton.create(icon, click, borderWidth, borderColor)
        },
        alpha = alpha,
        useKey = true,
    )
}
