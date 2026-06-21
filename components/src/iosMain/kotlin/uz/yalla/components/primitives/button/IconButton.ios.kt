package uz.yalla.components.primitives.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import uz.yalla.components.config.primitives.ICON_BUTTON_COLOR_UNSET
import uz.yalla.components.config.requireConfig

@OptIn(ExperimentalForeignApi::class)
@Composable
public actual fun IconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier,
    shape: IconButtonShape,
    iconColor: Color,
    containerColor: Color,
    borderColor: Color
) {
    val iconArgb = if (iconColor.isSpecified) iconColor.toArgb().toLong() else ICON_BUTTON_COLOR_UNSET
    val containerArgb = if (containerColor.isSpecified) containerColor.toArgb().toLong() else ICON_BUTTON_COLOR_UNSET
    val borderArgb = if (borderColor.isSpecified) borderColor.toArgb().toLong() else ICON_BUTTON_COLOR_UNSET
    val onClickState = rememberUpdatedState(onClick)

    val handle =
        remember {
            requireConfig().iconButton.create(
                icon = icon,
                shape = shape,
                iconArgb = iconArgb,
                containerArgb = containerArgb,
                borderArgb = borderArgb,
                onClick = { onClickState.value() }
            )
        }

    LaunchedEffect(icon) {
        handle.setIcon(icon)
    }

    LaunchedEffect(iconArgb, containerArgb, borderArgb) {
        handle.setColors(iconArgb, containerArgb, borderArgb)
    }

    UIKitViewController(
        factory = { handle.viewController },
        modifier = modifier
    )
}
