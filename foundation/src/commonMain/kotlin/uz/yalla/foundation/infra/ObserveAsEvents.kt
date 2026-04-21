package uz.yalla.foundation.infra

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Observes a [Flow] as one-time events, collecting only while the lifecycle is at least
 * [Lifecycle.State.STARTED].
 *
 * This is the **default overload** and covers the vast majority of cases: navigation,
 * snackbars, toasts, and other side effects that should only fire when the UI is visible.
 * If you need collection in a different lifecycle state (e.g. [Lifecycle.State.RESUMED]),
 * use the overload that accepts a [minState][Lifecycle.State] parameter.
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
 * @param key Optional recomposition key forwarded to [LaunchedEffect]. Defaults to [Unit].
 * @param onEvent Callback invoked for each emitted event
 * @see ObserveAsEvents overload accepting [Lifecycle.State] for custom minimum state
 * @since 0.0.1
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
 * Observes a [Flow] as one-time events with a **custom minimum lifecycle state**.
 *
 * Use this overload when the default [Lifecycle.State.STARTED] threshold is not appropriate.
 * For example, pass [Lifecycle.State.RESUMED] to ensure events are only processed while the
 * screen is fully in the foreground (useful for camera or sensor-related side effects), or
 * [Lifecycle.State.CREATED] if events must be collected even while the composable is in the
 * back stack.
 *
 * For the common case (STARTED), prefer the simpler overload without [minState].
 *
 * @param T Event type
 * @param flow Flow of events to observe
 * @param minState Minimum [Lifecycle.State] required for collection to be active
 * @param key Optional recomposition key forwarded to [LaunchedEffect]. Defaults to [Unit].
 * @param onEvent Callback invoked for each emitted event
 * @see ObserveAsEvents default overload that uses [Lifecycle.State.STARTED]
 * @since 0.0.1
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
