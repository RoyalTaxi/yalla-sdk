package uz.yalla.components.foundation.reactive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Observes a [Flow] as events, collecting only when lifecycle is at least STARTED.
 *
 * Use for one-time events like navigation, snackbars, or toasts that should
 * only be processed once and respect lifecycle state.
 *
 * ## Usage
 *
 * ```kotlin
 * ObserveAsEvents(viewModel.effects) { effect ->
 *     when (effect) {
 *         is Effect.Navigate -> navigator.navigate(effect.route)
 *         is Effect.ShowSnackbar -> snackbarHost.showSnackbar(effect.message)
 *     }
 * }
 * ```
 *
 * @param T Event type
 * @param flow Flow of events to observe
 * @param key Optional key for LaunchedEffect
 * @param onEvent Callback invoked for each event
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key: Any? = Unit,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner, flow, key) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { event ->
                onEvent(event)
            }
        }
    }
}

/**
 * Observes a [Flow] as events with custom minimum lifecycle state.
 *
 * @param T Event type
 * @param flow Flow of events to observe
 * @param minState Minimum lifecycle state for collection
 * @param key Optional key for LaunchedEffect
 * @param onEvent Callback invoked for each event
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    minState: Lifecycle.State,
    key: Any? = Unit,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner, flow, key) {
        lifecycleOwner.repeatOnLifecycle(minState) {
            flow.collect { event ->
                onEvent(event)
            }
        }
    }
}
