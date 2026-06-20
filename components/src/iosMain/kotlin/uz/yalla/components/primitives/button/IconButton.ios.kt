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
public actual fun IconButton(
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
    val borderArgb = if (borderColor.isSpecified) borderColor.toArgb().toLong() else 0L
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

    // TODO(quality, needs-decision): H1 — `borderArgb` is passed to create() but frozen afterward:
    //  `IconButtonHandle.setColors` omits it, so a dynamic border (error/focus) never updates on iOS
    //  (Android forwards `borderColor` live). The fix adds `borderArgb` to `setColors`, a BREAKING
    //  change to the committed `components.klib.api`. Blocked on owner sign-off.
    LaunchedEffect(iconArgb, containerArgb) {
        handle.setColors(iconArgb, containerArgb)
    }

    UIKitViewController(
        factory = { handle.viewController },
        modifier = modifier
    )
}
