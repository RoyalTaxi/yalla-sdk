package uz.yalla.composites.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System
import uz.yalla.design.theme.YallaTheme
import androidx.compose.material3.SnackbarHost as M3SnackbarHost

/**
 * Data for snackbar display.
 *
 * @param message Message text.
 * @param isSuccess Whether this is a success or error message.
 * @since 0.0.1
 */
data class SnackbarData(
    val message: String,
    val isSuccess: Boolean = true,
)

/**
 * Dimension configuration for [SnackbarHost].
 *
 * @param topPadding Top padding below status bar.
 * @param horizontalPadding Horizontal padding.
 * @since 0.0.1
 */
@Immutable
data class SnackbarHostDimens(
    val topPadding: Dp,
    val horizontalPadding: Dp,
)

/**
 * Snackbar host for displaying transient messages.
 *
 * Shows snackbars at the top of the screen with status bar padding.
 *
 * ## Usage
 *
 * ```kotlin
 * val snackbarHostState = remember { SnackbarHostState() }
 *
 * SnackbarHost(
 *     data = currentSnackbar,
 *     successIcon = rememberVectorPainter(YallaIcons.CheckCircle),
 *     errorIcon = rememberVectorPainter(YallaIcons.Warning),
 *     dismissIcon = rememberVectorPainter(YallaIcons.X),
 *     hostState = snackbarHostState,
 *     onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() },
 * )
 *
 * // Show snackbar
 * LaunchedEffect(event) {
 *     snackbarHostState.showSnackbar("Message")
 * }
 * ```
 *
 * @param data Current snackbar data to display.
 * @param successIcon Icon shown for success variant.
 * @param errorIcon Icon shown for error variant.
 * @param dismissIcon Icon for dismiss button.
 * @param hostState Material snackbar host state.
 * @param onDismiss Called when dismiss clicked.
 * @param modifier Applied to host.
 * @param dimens Dimension configuration, defaults to [SnackbarHostDefaults.dimens].
 *
 * @see SnackbarHostDefaults for default values
 * @since 0.0.1
 */
@Composable
fun SnackbarHost(
    data: SnackbarData?,
    successIcon: Painter,
    errorIcon: Painter,
    dismissIcon: Painter,
    hostState: SnackbarHostState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dimens: SnackbarHostDimens = SnackbarHostDefaults.dimens(),
) {
    M3SnackbarHost(
        hostState = hostState,
        modifier =
            modifier
                .statusBarsPadding()
                .padding(top = dimens.topPadding)
                .padding(horizontal = dimens.horizontalPadding),
    ) {
        if (data != null) {
            Snackbar(
                state =
                    SnackbarState(
                        message = data.message,
                        variant = if (data.isSuccess) SnackbarVariant.Success else SnackbarVariant.Error,
                        icon = if (data.isSuccess) successIcon else errorIcon,
                        dismissIcon = dismissIcon,
                    ),
                onDismiss = onDismiss,
            )
        }
    }
}

/**
 * Default values for [SnackbarHost].
 *
 * @since 0.0.1
 */
object SnackbarHostDefaults {
    /**
     * Creates default dimensions.
     *
     * @since 0.0.1
     */
    fun dimens(
        topPadding: Dp = 8.dp,
        horizontalPadding: Dp = 16.dp,
    ): SnackbarHostDimens =
        SnackbarHostDimens(
            topPadding = topPadding,
            horizontalPadding = horizontalPadding,
        )
}

@Preview
@Composable
private fun SnackbarHostPreview() {
    YallaTheme {
        Box(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(16.dp)
        ) {
            Text(
                text = "Snackbar Preview",
                style = System.font.body.base.medium,
            )
        }
    }
}
