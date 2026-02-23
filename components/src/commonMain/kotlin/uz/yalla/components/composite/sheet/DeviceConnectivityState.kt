package uz.yalla.components.composite.sheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.jordond.connectivity.Connectivity
import dev.jordond.connectivity.Connectivity.Status.Connected
import dev.jordond.connectivity.Connectivity.Status.Disconnected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * State holder for device connectivity monitoring.
 *
 * ## Usage
 *
 * ```kotlin
 * val connectivity = Connectivity()
 * val connectivityState = rememberDeviceConnectivityState(connectivity)
 *
 * LaunchedEffect(Unit) {
 *     connectivityState.startMonitoring()
 * }
 *
 * if (connectivityState.isDisconnected) {
 *     NoInternetBanner()
 * }
 * ```
 *
 * @param connectivity Connectivity instance
 * @param scope Coroutine scope for collecting updates
 */
@Stable
class DeviceConnectivityState(
    private val connectivity: Connectivity,
    private val scope: CoroutineScope
) {
    var isMonitoring: Boolean by mutableStateOf(connectivity.monitoring.value)
        private set

    var status: Connectivity.Status? by mutableStateOf(null)
        private set

    val isConnected: Boolean
        get() = status is Connected

    val isDisconnected: Boolean
        get() = status is Disconnected

    fun forceCheck() {
        scope.launch { connectivity.status() }
    }

    fun startMonitoring() {
        connectivity.start()
    }

    fun stopMonitoring() {
        connectivity.stop()
    }

    init {
        scope.launch {
            connectivity.monitoring.collect { isMonitoring = it }
        }
        scope.launch {
            connectivity.statusUpdates.collect { status = it }
        }
    }
}

/**
 * Remember a [DeviceConnectivityState] for monitoring network connectivity.
 *
 * @param connectivity Connectivity instance
 * @param scope Coroutine scope for collecting updates
 * @return Remembered connectivity state
 */
@Composable
fun rememberDeviceConnectivityState(
    connectivity: Connectivity,
    scope: CoroutineScope = rememberCoroutineScope()
): DeviceConnectivityState {
    return remember(connectivity, scope) {
        DeviceConnectivityState(connectivity, scope)
    }
}
