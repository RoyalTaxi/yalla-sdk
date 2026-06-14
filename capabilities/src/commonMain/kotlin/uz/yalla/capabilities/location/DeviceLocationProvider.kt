package uz.yalla.capabilities.location

import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.LocationProvider

public class DeviceLocationProvider(
    public val locationTracker: LocationTracker,
    private val scope: CoroutineScope
) : LocationProvider {
    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    override val currentLocation: StateFlow<GeoPoint?> = _currentLocation.asStateFlow()

    private val _permissionState = MutableStateFlow<LocationPermissionState?>(null)
    public val permissionState: StateFlow<LocationPermissionState?> = _permissionState.asStateFlow()

    private var job: Job? = null

    override fun startTracking() {
        if (job != null) return
        job = scope.launch {
            try {
                locationTracker.startTracking()
                locationTracker.getLocationsFlow().collect { location ->
                    _currentLocation.value = GeoPoint(location.latitude, location.longitude)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                job = null
            }
        }
    }

    override fun stopTracking() {
        job?.cancel()
        job = null
        locationTracker.stopTracking()
    }

    public fun updatePermissionState(state: LocationPermissionState?) {
        _permissionState.value = state
    }
}
