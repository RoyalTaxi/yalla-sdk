package uz.yalla.platform.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.yalla.platform.model.IconType

/**
 * Circular icon button with platform-native rendering.
 *
 * On iOS, renders as a UIKit-backed circular button for native hit-testing and
 * accessibility. On Android, renders as a Material3 `IconButton` with a circular shape.
 *
 * ## Usage
 * ```kotlin
 * NativeCircleIconButton(
 *     iconType = IconType.FOCUS_LOCATION,
 *     onClick = { viewModel.focusOnUserLocation() },
 *     background = System.color.background.primary,
 * )
 * ```
 *
 * @param alpha Opacity of the entire button. Default `1f` (fully opaque).
 * @param background Background fill color. [Color.Unspecified] uses the platform default.
 */
@Composable
expect fun NativeCircleIconButton(
    iconType: IconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    border: BorderStroke? = null,
    background: Color = Color.Unspecified
)
