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
import uz.yalla.components.config.requireConfig

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun IconButton(
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier,
    shape: IconButtonShape,
    iconColor: Color,
    containerColor: Color,
    borderColor: Color
) {
    val iconArgb = if (iconColor.isSpecified) iconColor.toArgb().toLong() else 0L
    val containerArgb = if (containerColor.isSpecified) containerColor.toArgb().toLong() else 0L
    val onClickState = rememberUpdatedState(onClick)

    val handle = remember {
        requireConfig().iconButton.create(
            icon = icon,
            shape = shape,
            iconArgb = iconArgb,
            containerArgb = containerArgb,
            onClick = { onClickState.value() }
        )
    }

    LaunchedEffect(icon) {
        handle.setIcon(icon)
    }

    LaunchedEffect(iconArgb, containerArgb) {
        handle.setColors(iconArgb, containerArgb)
    }

    UIKitViewController(
        factory = { handle.viewController },
        modifier = modifier
    )
}
