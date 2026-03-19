package uz.yalla.platform.indicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Platform-native loading/progress indicator.
 *
 * On iOS, renders a `UIActivityIndicatorView` spinner.
 * On Android, renders a Material3 `CircularProgressIndicator`.
 *
 * ## Usage
 * ```kotlin
 * if (isLoading) {
 *     NativeLoadingIndicator(
 *         color = System.color.accent.primary,
 *     )
 * }
 * ```
 *
 * @param modifier Modifier applied to the indicator container.
 * @param color Foreground spinner color. [Color.Unspecified] uses the platform default.
 * @param backgroundColor Background color behind the spinner. [Color.Unspecified] for transparent.
 * @since 0.0.1
 */
@Composable
expect fun NativeLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified
)
