package uz.yalla.composites.sheet

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import uz.yalla.composites.snackbar.AppSnackbarHost
import uz.yalla.composites.snackbar.AppSnackbarHostState
import uz.yalla.composites.snackbar.SnackbarData
import uz.yalla.resources.Res
import uz.yalla.resources.ic_check_circle
import uz.yalla.resources.ic_warning
import uz.yalla.resources.ic_x

/**
 * State holder for sheet-local snackbar.
 *
 * @see rememberSheetSnackbarState
 * @see SheetSnackbarHost
 */
class SheetSnackbarState(
    val hostState: SnackbarHostState,
) {
    var data by mutableStateOf<SnackbarData?>(null)
        internal set

    suspend fun show(data: SnackbarData) {
        this.data = data
        hostState.currentSnackbarData?.dismiss()
        hostState.showSnackbar(
            message = data.message,
            duration = SnackbarDuration.Long,
        )
        this.data = null
    }

    suspend fun show(message: String, isSuccess: Boolean = false) {
        show(SnackbarData(message = message, isSuccess = isSuccess))
    }

    fun dismiss() {
        hostState.currentSnackbarData?.dismiss()
        data = null
    }
}

@Composable
fun rememberSheetSnackbarState(): SheetSnackbarState {
    val hostState = remember { SnackbarHostState() }
    return remember { SheetSnackbarState(hostState) }
}

/**
 * Snackbar host for use inside [Sheet]'s `snackbarHost` slot.
 *
 * ## Usage
 *
 * ```kotlin
 * val snackbar = rememberSheetSnackbarState()
 *
 * LaunchedEffect(errorState) {
 *     if (errorState != null) {
 *         snackbar.show(errorState.message, isSuccess = false)
 *     }
 * }
 *
 * Sheet(
 *     snackbarHost = { SheetSnackbarHost(snackbar) },
 * ) { content() }
 * ```
 */
@Composable
fun SheetSnackbarHost(
    state: SheetSnackbarState,
    modifier: Modifier = Modifier,
) {
    AppSnackbarHost(
        state = AppSnackbarHostState(
            data = state.data,
            successIcon = painterResource(Res.drawable.ic_check_circle),
            errorIcon = painterResource(Res.drawable.ic_warning),
            dismissIcon = painterResource(Res.drawable.ic_x),
        ),
        hostState = state.hostState,
        onDismiss = { state.dismiss() },
        modifier = modifier,
    )
}
