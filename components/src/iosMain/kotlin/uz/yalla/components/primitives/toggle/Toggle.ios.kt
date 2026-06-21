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
import uz.yalla.components.config.primitives.ToggleDefaults
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
    val colors =
        ToggleDefaults.colors(
            checkedThumbArgb = checkedThumbColor.toArgbOrZero(),
            checkedTrackArgb = checkedTrackColor.toArgbOrZero(),
            checkedBorderArgb = checkedBorderColor.toArgbOrZero(),
            uncheckedThumbArgb = uncheckedThumbColor.toArgbOrZero(),
            uncheckedTrackArgb = uncheckedTrackColor.toArgbOrZero(),
            uncheckedBorderArgb = uncheckedBorderColor.toArgbOrZero(),
            disabledCheckedThumbArgb = disabledCheckedThumbColor.toArgbOrZero(),
            disabledCheckedTrackArgb = disabledCheckedTrackColor.toArgbOrZero(),
            disabledCheckedBorderArgb = disabledCheckedBorderColor.toArgbOrZero(),
            disabledUncheckedThumbArgb = disabledUncheckedThumbColor.toArgbOrZero(),
            disabledUncheckedTrackArgb = disabledUncheckedTrackColor.toArgbOrZero(),
            disabledUncheckedBorderArgb = disabledUncheckedBorderColor.toArgbOrZero()
        )
    val onCheckedChangeState = rememberUpdatedState(onCheckedChange)

    val handle =
        remember {
            requireConfig().toggle.create(
                initialChecked = checked,
                initialEnabled = enabled,
                colors = colors,
                onCheckedChange = { onCheckedChangeState.value(it) }
            )
        }

    LaunchedEffect(checked) { handle.setChecked(checked) }
    LaunchedEffect(enabled) { handle.setEnabled(enabled) }
    LaunchedEffect(
        colors.checkedThumbArgb,
        colors.checkedTrackArgb,
        colors.checkedBorderArgb,
        colors.uncheckedThumbArgb,
        colors.uncheckedTrackArgb,
        colors.uncheckedBorderArgb,
        colors.disabledCheckedThumbArgb,
        colors.disabledCheckedTrackArgb,
        colors.disabledCheckedBorderArgb,
        colors.disabledUncheckedThumbArgb,
        colors.disabledUncheckedTrackArgb,
        colors.disabledUncheckedBorderArgb
    ) { handle.setColors(colors) }

    UIKitViewController(
        factory = { handle.viewController },
        modifier = modifier
    )
}

private fun Color.toArgbOrZero(): Long = if (isSpecified) toArgb().toLong() else 0L
