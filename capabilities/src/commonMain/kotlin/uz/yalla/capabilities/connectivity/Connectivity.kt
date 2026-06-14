package uz.yalla.capabilities.connectivity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.jordond.connectivity.Connectivity
import dev.jordond.connectivity.ConnectivityOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
public class ConnectivityState(
    private val connectivity: Connectivity,
    private val scope: CoroutineScope
) {
    public var isOnline: Boolean by mutableStateOf(true)
        private set

    init {
        scope.launch {
            connectivity.statusUpdates.collect { isOnline = it is Connectivity.Status.Connected }
        }
    }

    public fun refresh(): Job = scope.launch {
        isOnline = connectivity.status() is Connectivity.Status.Connected
    }
}

@Composable
public fun rememberConnectivityState(
    connectivity: Connectivity,
    scope: CoroutineScope = rememberCoroutineScope()
): ConnectivityState = remember(connectivity, scope) {
    ConnectivityState(connectivity, scope)
}

@Composable
public fun rememberConnectivityState(
    scope: CoroutineScope = rememberCoroutineScope()
): ConnectivityState {
    val connectivity = remember(scope) {
        Connectivity(options = ConnectivityOptions.build { autoStart = true }, scope = scope)
    }
    return rememberConnectivityState(connectivity, scope)
}
