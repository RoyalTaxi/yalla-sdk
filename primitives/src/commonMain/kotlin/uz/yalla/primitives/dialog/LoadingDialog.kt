package uz.yalla.primitives.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import uz.yalla.platform.indicator.NativeLoadingIndicator

/**
 * Color configuration for [LoadingDialog].
 *
 * @param container Background color.
 * @param indicator Spinner color.
 * @since 0.0.1
 */
@Immutable
data class LoadingDialogColors(
    val container: Color,
    val indicator: Color,
)

/**
 * Dimension configuration for [LoadingDialog].
 *
 * @param contentPadding Padding inside dialog.
 * @param shape Dialog shape.
 * @since 0.0.1
 */
@Immutable
data class LoadingDialogDimens(
    val contentPadding: Dp,
    val shape: Shape,
)

/**
 * Modal loading dialog with centered spinner.
 *
 * Use to block interaction during critical operations.
 *
 * ## Usage
 *
 * ```kotlin
 * if (state.isLoading) {
 *     LoadingDialog()
 * }
 * ```
 *
 * @param modifier Applied to dialog content.
 * @param onDismissRequest Called when user tries to dismiss (optional).
 * @param dismissOnBackPress Whether back press dismisses dialog.
 * @param dismissOnClickOutside Whether clicking outside dismisses dialog.
 * @param colors Color configuration, defaults to [LoadingDialogDefaults.colors].
 * @param dimens Dimension configuration, defaults to [LoadingDialogDefaults.dimens].
 *
 * @see uz.yalla.primitives.indicator.LoadingIndicator for inline loading indicator
 * @see LoadingDialogDefaults for default values
 * @since 0.0.1
 */
@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = false,
    colors: LoadingDialogColors = LoadingDialogDefaults.colors(),
    dimens: LoadingDialogDimens = LoadingDialogDefaults.dimens(),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
            ),
    ) {
        Box(
            modifier =
                modifier
                    .background(
                        color = colors.container,
                        shape = dimens.shape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            NativeLoadingIndicator(
                modifier = Modifier.padding(dimens.contentPadding),
                color = colors.indicator,
                backgroundColor = colors.container,
            )
        }
    }
}

/**
 * Default configuration values for [LoadingDialog].
 *
 * Provides theme-aware defaults for [colors] and [dimens] that can be overridden.
 * @since 0.0.1
 */
object LoadingDialogDefaults {
    /** Creates color configuration for [LoadingDialog]. */
    @Composable
    fun colors(
        container: Color = Color.White,
        indicator: Color = System.color.background.brand,
    ): LoadingDialogColors = LoadingDialogColors(
        container = container,
        indicator = indicator,
    )

    /** Creates dimension configuration for [LoadingDialog]. */
    fun dimens(
        contentPadding: Dp = 20.dp,
        shape: Shape = CircleShape,
    ): LoadingDialogDimens = LoadingDialogDimens(
        contentPadding = contentPadding,
        shape = shape,
    )
}

@Preview
@Composable
private fun LoadingDialogContentPreview() {
    YallaTheme {
        Box(
            modifier =
                Modifier
                    .background(
                        color = Color.White,
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.padding(20.dp))
        }
    }
}
