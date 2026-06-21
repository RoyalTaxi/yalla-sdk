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

/**
 * Creates a [DeviceLocationProvider] backed by the platform location tracker.
 *
 * The supplied [scope] **must be confined to the main thread** (e.g.
 * `CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)`): starting
 * tracking touches the OS permission / Activity flow, which is main-thread only,
 * and the provider's internal state is not synchronized for arbitrary
 * multi-threaded scopes. Tracking is bound to the scope's lifetime; cancel the
 * scope (or call [DeviceLocationProvider.stopTracking]) to release the OS session.
 */
public fun createDeviceLocationProvider(scope: CoroutineScope): DeviceLocationProvider =
    DeviceLocationProvider(createLocationTracker(), scope)

/**
 * [LocationProvider] over the platform GPS/fused-location tracker.
 *
 * [startTracking] is idempotent while a session is active and self-recovers on
 * failure (the OS session is released and the guard cleared so a later start
 * retries). Must be driven from a main-confined scope — see
 * [createDeviceLocationProvider].
 */
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
                    // Tracking failed after the OS session may have started; release it so
                    // the sensor isn't left running. The job is cleared in invokeOnCompletion
                    // (not from here) to avoid clobbering the assignment below.
                    runCatching { locationTracker.stopTracking() }
                }
            }
        // Publish the job BEFORE registering the completion handler. If the body already
        // finished (e.g. a synchronous failure on Main.immediate), invokeOnCompletion fires
        // immediately and — because job already === launched — clears the guard, instead of
        // leaving it pointing at a dead job and wedging every later startTracking().
        job = launched
        launched.invokeOnCompletion { if (job === launched) job = null }
    }

    override fun stopTracking() {
        job?.cancel()
        job = null
        locationTracker.stopTracking()
    }
}
