package uz.yalla.components.primitives.toggle

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
public actual fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    checkedThumbColor: Color,
    checkedTrackColor: Color,
    checkedBorderColor: Color,
    uncheckedThumbColor: Color,
    uncheckedTrackColor: Color,
    uncheckedBorderColor: Color,
    disabledCheckedThumbColor: Color,
    disabledCheckedTrackColor: Color,
    disabledCheckedBorderColor: Color,
    disabledUncheckedThumbColor: Color,
    disabledUncheckedTrackColor: Color,
    disabledUncheckedBorderColor: Color
) {
    val thumbArgb = uncheckedThumbColor.toArgbOrZero()
    val trackArgb = checkedTrackColor.toArgbOrZero()
    val onCheckedChangeState = rememberUpdatedState(onCheckedChange)

    val handle =
        remember {
            requireConfig().toggle.create(
                initialChecked = checked,
                initialEnabled = enabled,
                thumbArgb = thumbArgb,
                trackArgb = trackArgb,
                onCheckedChange = { onCheckedChangeState.value(it) }
            )
        }

    LaunchedEffect(checked) { handle.setChecked(checked) }
    LaunchedEffect(enabled) { handle.setEnabled(enabled) }
    LaunchedEffect(thumbArgb, trackArgb) { handle.setColors(thumbArgb, trackArgb) }

    UIKitViewController(
        factory = { handle.viewController },
        modifier = modifier
    )
}

private fun Color.toArgbOrZero(): Long = if (isSpecified) toArgb().toLong() else 0L
