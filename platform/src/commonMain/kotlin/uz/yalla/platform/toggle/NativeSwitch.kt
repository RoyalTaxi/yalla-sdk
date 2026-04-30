package uz.yalla.platform.toggle

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-native toggle switch.
 *
 * On iOS, renders a `UISwitch` for the familiar iOS toggle appearance.
 * On Android, renders a Material3 `Switch`.
 *
 * ## Usage
 * ```kotlin
 * NativeSwitch(
 *     checked = isDarkMode,
 *     onCheckedChange = { viewModel.setDarkMode(it) },
 * )
 * ```
 *
 * @param enabled Whether the switch is interactive. Default `true`.
 */
@Composable
expect fun NativeSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
