package uz.yalla.composites.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * One-shot event for snackbar display.
 *
 * @see SnackbarController
 */
sealed interface SnackbarEvent {
    /** Show a snackbar with the given data. */
    data class Show(val data: SnackbarData) : SnackbarEvent

    /** Dismiss the current snackbar. */
    data object Dismiss : SnackbarEvent
}

/**
 * Global controller for snackbar events.
 *
 * Uses a [Channel] internally so events are:
 * - never replayed on lifecycle restart (no stale messages)
 * - never deduplicated (identical messages both show)
 * - consumed exactly once
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
    private val channel = Channel<SnackbarEvent>(Channel.BUFFERED)

    /** Stream of one-shot snackbar events. Collect with [ObserveAsEvents]. */
    val events: Flow<SnackbarEvent> = channel.receiveAsFlow()

    /**
     * Shows a snackbar with the given data.
     *
     * @param data Snackbar content and style.
     */
    fun show(data: SnackbarData) {
        channel.trySend(SnackbarEvent.Show(data))
    }

    /**
     * Dismisses the current snackbar.
     */
    fun dismiss() {
        channel.trySend(SnackbarEvent.Dismiss)
    }

    /**
     * Sets snackbar data directly.
     *
     * @param event Snackbar data, null to dismiss.
     */
    fun sendData(event: SnackbarData?) {
        if (event != null) {
            channel.trySend(SnackbarEvent.Show(event))
        } else {
            channel.trySend(SnackbarEvent.Dismiss)
        }
    }
}
