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

public fun createDeviceLocationProvider(scope: CoroutineScope): DeviceLocationProvider =
    DeviceLocationProvider(createLocationTracker(), scope)

public class DeviceLocationProvider internal constructor(
    internal val locationTracker: LocationTracker,
    private val scope: CoroutineScope
) : LocationProvider {
    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    override val currentLocation: StateFlow<GeoPoint?> = _currentLocation.asStateFlow()

    private var job: Job? = null

    override fun startTracking() {
        if (job != null) return
        val launched =
            scope.launch {
                try {
                    locationTracker.startTracking()
                    locationTracker.getLocationsFlow().collect { location ->
                        _currentLocation.value = GeoPoint(location.latitude, location.longitude)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (ignored: Exception) {
                    runCatching { locationTracker.stopTracking() }
                }
            }
        job = launched
        launched.invokeOnCompletion { if (job === launched) job = null }
    }

    override fun stopTracking() {
        job?.cancel()
        job = null
        locationTracker.stopTracking()
    }
}
