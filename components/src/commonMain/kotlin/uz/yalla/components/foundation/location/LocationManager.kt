package uz.yalla.components.foundation.location

import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.LocationProvider
import uz.yalla.core.geo.GeoPoint

/**
 * Manages device location tracking and permission state.
 *
 * Implements [LocationProvider] so it can be used directly by the maps module
 * without requiring a wrapper/adapter class.
 *
 * Provides reactive location updates with extended metadata (accuracy, speed, bearing)
 * and handles permission state tracking. Uses [SupervisorJob] for independent lifecycle management.
 *
 * ## Usage
 *
 * ```kotlin
 * val locationManager = LocationManager(locationTracker)
 *
 * // Start tracking
 * locationManager.startTracking()
 *
 * // Observe location updates (as GeoPoint for maps)
 * locationManager.currentLocation.collect { point ->
 *     point?.let { updateMap(it) }
 * }
 *
 * // Or observe extended location data
 * locationManager.extendedLocation.collect { location ->
 *     location?.let { updateUI(it.accuracy, it.speed) }
 * }
 *
 * // Stop when done
 * locationManager.stopTracking()
 *
 * // Clean up when no longer needed
 * locationManager.close()
 * ```
 *
 * @param locationTracker Platform-specific location tracker from moko-geo
 * @param defaultLocation Fallback location when user location is unavailable
 */
class LocationManager(
    val locationTracker: LocationTracker,
    private val defaultLocation: GeoPoint = DEFAULT_LOCATION,
) : LocationProvider {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _extendedLocation = MutableStateFlow<ExtendedLocation?>(null)

    /** Current device location with extended metadata. Null if location unavailable or tracking stopped. */
    val extendedLocation: StateFlow<ExtendedLocation?> = _extendedLocation.asStateFlow()

    /** Current location as [GeoPoint]. Emits null if location unavailable. */
    override val currentLocation: Flow<GeoPoint?> =
        _extendedLocation.map { it?.toGeoPoint() }

    private val _isTracking = MutableStateFlow(false)

    /** Whether location tracking is currently active. */
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _permissionState = MutableStateFlow<LocationPermissionState?>(null)

    /** Current location permission state. Null if not yet checked. */
    val permissionState: StateFlow<LocationPermissionState?> = _permissionState.asStateFlow()

    /** Starts location tracking. Requires location permission. */
    override fun startTracking() {
        if (_isTracking.value) return

        scope.launch {
            runCatching {
                locationTracker.startTracking()
                _isTracking.value = true

                locationTracker
                    .getExtendedLocationsFlow()
                    .distinctUntilChanged()
                    .collect { extLoc ->
                        _extendedLocation.value =
                            ExtendedLocation(
                                latitude = extLoc.location.coordinates.latitude,
                                longitude = extLoc.location.coordinates.longitude,
                                accuracy = extLoc.location.coordinatesAccuracyMeters.toFloat(),
                                altitude = extLoc.altitude.altitudeMeters,
                                speed = extLoc.speed.speedMps.toFloat(),
                                bearing = extLoc.azimuth.azimuthDegrees.toFloat(),
                                timestamp = extLoc.timestampMs,
                            )
                    }
            }.onFailure {
                _isTracking.value = false
            }
        }
    }

    /** Stops location tracking. */
    override fun stopTracking() {
        if (!_isTracking.value) return

        scope.launch {
            runCatching {
                locationTracker.stopTracking()
                _isTracking.value = false
            }
        }
    }

    /**
     * Updates the permission state.
     *
     * @param state New permission state, or null if unknown
     */
    fun updatePermissionState(state: LocationPermissionState?) {
        _permissionState.value = state
    }

    /** Returns current location as [GeoPoint], or null if unavailable. */
    override fun getCurrentLocation(): GeoPoint? = _extendedLocation.value?.toGeoPoint()

    /** Returns current location as [GeoPoint], or [defaultLocation] if unavailable. */
    override fun getCurrentLocationOrDefault(): GeoPoint = getCurrentLocation() ?: defaultLocation

    /** Cancels the internal coroutine scope. Call when this manager is no longer needed. */
    fun close() {
        scope.cancel()
    }

    companion object {
        /** Default fallback location: Tashkent, Uzbekistan. */
        val DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)
    }
}
