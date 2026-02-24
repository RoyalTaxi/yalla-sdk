package uz.yalla.composites.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uz.yalla.design.theme.System

/**
 * Data for snackbar display.
 *
 * @param message Message text.
 * @param isSuccess Whether this is a success or error message.
 */
data class SnackbarData(
    val message: String,
    val isSuccess: Boolean = true,
)

/**
 * UI state for [AppSnackbarHost].
 *
 * @param data Current snackbar data to display.
 * @param successIcon Icon shown for success variant.
 * @param errorIcon Icon shown for error variant.
 * @param dismissIcon Icon for dismiss button.
 */
data class AppSnackbarHostState(
    val data: SnackbarData?,
    val successIcon: Painter,
    val errorIcon: Painter,
    val dismissIcon: Painter,
)

/**
 * Default configuration for [AppSnackbarHost].
 *
 * Provides theme-aware defaults for [dimens] that can be overridden.
 */
object AppSnackbarHostDefaults {
    /**
     * Dimension configuration for [AppSnackbarHost].
     *
     * @param topPadding Top padding below status bar.
     * @param horizontalPadding Horizontal padding.
     */
    data class AppSnackbarHostDimens(
        val topPadding: Dp,
        val horizontalPadding: Dp,
    )

    @Composable
    fun dimens(
        topPadding: Dp = 8.dp,
        horizontalPadding: Dp = 16.dp,
    ) = AppSnackbarHostDimens(
        topPadding = topPadding,
        horizontalPadding = horizontalPadding,
    )
}

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
 * AppSnackbarHost(
 *     state = AppSnackbarHostState(
 *         data = currentSnackbar,
 *         successIcon = painterResource(Res.drawable.ic_check_circle),
 *         errorIcon = painterResource(Res.drawable.ic_warning),
 *         dismissIcon = painterResource(Res.drawable.ic_x),
 *     ),
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
 * @param state Current UI state with data and icons.
 * @param hostState Material snackbar host state.
 * @param onDismiss Called when dismiss clicked.
 * @param modifier Applied to host.
 * @param dimens Dimension configuration, defaults to [AppSnackbarHostDefaults.dimens].
 *
 * @see AppSnackbarHostState for state configuration
 * @see AppSnackbarHostDefaults for default values
 */
@Composable
fun AppSnackbarHost(
    state: AppSnackbarHostState,
    hostState: SnackbarHostState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dimens: AppSnackbarHostDefaults.AppSnackbarHostDimens = AppSnackbarHostDefaults.dimens(),
) {
    SnackbarHost(
        hostState = hostState,
        modifier =
            modifier
                .statusBarsPadding()
                .padding(top = dimens.topPadding)
                .padding(horizontal = dimens.horizontalPadding),
    ) {
        if (state.data != null) {
            Snackbar(
                state =
                    SnackbarState(
                        message = state.data.message,
                        variant = if (state.data.isSuccess) SnackbarVariant.Success else SnackbarVariant.Error,
                        icon = if (state.data.isSuccess) state.successIcon else state.errorIcon,
                        dismissIcon = state.dismissIcon,
                    ),
                onDismiss = onDismiss,
            )
        }
    }
}

@Preview
@Composable
private fun SnackbarHostPreview() {
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
