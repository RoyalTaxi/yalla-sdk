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

@Stable
public class ConnectivityState internal constructor(
    private val connectivity: Connectivity,
    private val scope: CoroutineScope
) {
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

@Composable
public fun rememberConnectivityState(scope: CoroutineScope = rememberCoroutineScope()): ConnectivityState {
    val connectivity = remember(scope) { defaultConnectivity(scope) }
    return rememberConnectivityState(connectivity, scope)
}

private fun defaultConnectivity(scope: CoroutineScope): Connectivity =
    Connectivity(options = ConnectivityOptions.build { autoStart = true }, scope = scope)
