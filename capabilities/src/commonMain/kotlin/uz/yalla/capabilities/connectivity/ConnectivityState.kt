package uz.yalla.capabilities.connectivity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import dev.jordond.connectivity.Connectivity
import dev.jordond.connectivity.ConnectivityOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Observable online/offline state backed by the platform network monitor.
 *
 * The supplied `scope`'s cancellation is the **only** teardown: the underlying OS
 * monitor (Android `NetworkCallback` / iOS `NWPathMonitor`) and the status collector
 * are released when the scope is cancelled. Pass a scope tied to the consuming
 * lifecycle (e.g. `rememberCoroutineScope()`); do **not** pass an
 * application/DI-singleton scope, or the monitor will never be unregistered.
 */
@Stable
public class ConnectivityState internal constructor(
    private val connectivity: Connectivity,
    private val scope: CoroutineScope
) {
    /** `true` while the device has connectivity. */
    public var isOnline: Boolean by mutableStateOf(true)
        private set

    private var refreshJob: Job? = null

    init {
        scope.launch {
            connectivity.statusUpdates
                .catch { Logger.e(it) { "Connectivity status stream failed" } }
                .collect { isOnline = it is Connectivity.Status.Connected }
        }
    }

    /**
     * Re-reads the current connectivity status. Single-flight: a pending refresh is
     * cancelled before a new one starts, so repeated taps can't race stale writes
     * into [isOnline].
     */
    public fun refresh(): Job {
        refreshJob?.cancel()
        return scope
            .launch {
                isOnline = connectivity.status() is Connectivity.Status.Connected
            }.also { refreshJob = it }
    }
}

@Composable
internal fun rememberConnectivityState(
    connectivity: Connectivity,
    scope: CoroutineScope = rememberCoroutineScope()
): ConnectivityState =
    remember(connectivity, scope) {
        ConnectivityState(connectivity, scope)
    }

/**
 * Remembers a [ConnectivityState] that monitors network reachability for the
 * lifetime of [scope].
 *
 * @param scope drives teardown of the OS monitor — see [ConnectivityState]. Defaults
 *   to a composition-scoped scope.
 */
@Composable
public fun rememberConnectivityState(scope: CoroutineScope = rememberCoroutineScope()): ConnectivityState {
    val connectivity = remember(scope) { defaultConnectivity(scope) }
    return rememberConnectivityState(connectivity, scope)
}

/** Builds the SDK's default [Connectivity] source; keeps the vendor type an impl detail. */
private fun defaultConnectivity(scope: CoroutineScope): Connectivity =
    Connectivity(options = ConnectivityOptions.build { autoStart = true }, scope = scope)
