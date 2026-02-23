package uz.yalla.components.composite.snackbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global controller for snackbar events.
 *
 * Use to show/dismiss snackbars from anywhere in the app.
 *
 * ## Usage
 *
 * ```kotlin
 * // Show success
 * SnackbarController.show(SnackbarData("Saved!", isSuccess = true))
 *
 * // Show error
 * SnackbarController.show(SnackbarData("Failed to save", isSuccess = false))
 *
 * // Dismiss
 * SnackbarController.dismiss()
 * ```
 *
 * @see SnackbarData
 * @see AppSnackbarHost
 */
object SnackbarController {
    private val _data = MutableStateFlow<SnackbarData?>(null)

    /** Current snackbar data, null if dismissed. */
    val data = _data.asStateFlow()

    /**
     * Shows a snackbar with the given data.
     *
     * @param data Snackbar content and style.
     */
    fun show(data: SnackbarData) {
        _data.value = data
    }

    /**
     * Dismisses the current snackbar.
     */
    fun dismiss() {
        _data.value = null
    }

    /**
     * Sets snackbar data directly.
     *
     * @param event Snackbar data, null to dismiss.
     */
    fun sendData(event: SnackbarData?) {
        _data.value = event
    }
}
