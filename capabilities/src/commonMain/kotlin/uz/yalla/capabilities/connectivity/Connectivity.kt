package uz.yalla.capabilities.connectivity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.jordond.connectivity.Connectivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class ConnectivityState(
    private val connectivity: Connectivity,
    private val scope: CoroutineScope
) {
    var isOnline: Boolean by mutableStateOf(true)
        private set

    init {
        scope.launch {
            connectivity.statusUpdates.collect { isOnline = it is Connectivity.Status.Connected }
        }
    }

    fun refresh() = scope.launch {
        isOnline = connectivity.status() is Connectivity.Status.Connected
    }
}

@Composable
fun rememberConnectivityState(
    connectivity: Connectivity,
    scope: CoroutineScope = rememberCoroutineScope()
): ConnectivityState = remember(connectivity, scope) {
    ConnectivityState(connectivity, scope)
}
