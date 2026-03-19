package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.platform.model.IconType

/**
 * Squircle-shaped (rounded-square) icon button with platform-native rendering.
 *
 * On iOS, renders as a UIKit-backed squircle button using continuous corner curves.
 * On Android, renders as a Material3 button with a `RoundedCornerShape`.
 *
 * ## Usage
 * ```kotlin
 * NativeSquircleIconButton(
 *     iconType = IconType.MENU,
 *     onClick = { navigator.push(AppRoute.Menu) },
 * )
 * ```
 *
 * @param iconType The icon to display inside the button.
 * @param onClick Callback invoked when the button is tapped.
 * @param modifier Modifier applied to the button container.
 * @param border Optional border stroke drawn around the squircle.
 * @param background Background fill color. [Color.Unspecified] uses the platform default.
 * @since 0.0.1
 */
@Composable
expect fun NativeSquircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null,
    background: Color = Color.Unspecified
)
