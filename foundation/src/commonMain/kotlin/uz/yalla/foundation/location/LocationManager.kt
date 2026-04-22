package uz.yalla.foundation.location

import co.touchlab.kermit.Logger
import dev.icerock.moko.geo.LocationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uz.yalla.core.contract.location.LocationProvider
import uz.yalla.core.geo.GeoPoint

/**
 * Manages device location tracking and permission state.
 *
 * Implements [LocationProvider] so it can be injected directly into the maps module.
 *
 * ## Scope ownership (ADR-013)
 *
 * `LocationManager` does **not** own its [CoroutineScope]. The caller constructs and
 * cancels the scope — typically a process-lifetime `SupervisorJob` held in the DI
 * container. When that scope is cancelled, all in-flight tracking operations stop.
 *
 * There is no `close()` method: the scope's lifecycle *is* the lifecycle.
 *
 * ## Usage
 *
 * ```kotlin
 * val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
 * val lm = LocationManager(locationTracker, scope)
 *
 * lm.startTracking()
 * lm.currentLocation.collect { point -> /* ... */ }
 *
 * // On teardown:
 * scope.cancel()
 * ```
 *
 * @param locationTracker Platform-specific tracker from moko-geo.
 * @param scope Caller-owned scope; cancel to stop all in-flight tracking work.
 * @param defaultLocation Fallback when user location is unavailable.
 * @since 0.0.10
 */
class LocationManager(
    val locationTracker: LocationTracker,
    private val scope: CoroutineScope,
    private val defaultLocation: GeoPoint = DEFAULT_LOCATION,
) : LocationProvider {

    private val _extendedLocation = MutableStateFlow<ExtendedLocation?>(null)

    /**
     * Current device location with extended metadata, or `null` if tracking is off
     * or no fix has arrived yet.
     *
     * @since 0.0.1
     */
    val extendedLocation: StateFlow<ExtendedLocation?> = _extendedLocation.asStateFlow()

    /**
     * Current location as [GeoPoint]; emits `null` if no fix.
     *
     * @since 0.0.1
     */
    override val currentLocation: Flow<GeoPoint?> = _extendedLocation.map { it?.toGeoPoint() }

    private val _isTracking = MutableStateFlow(false)

    /**
     * `true` while tracking is active (between [startTracking] and [stopTracking] or
     * scope cancellation).
     *
     * @since 0.0.1
     */
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _permissionState = MutableStateFlow<LocationPermissionState?>(null)

    /**
     * Last-observed permission state; `null` until the first [updatePermissionState] call.
     *
     * @since 0.0.1
     */
    val permissionState: StateFlow<LocationPermissionState?> = _permissionState.asStateFlow()

    /**
     * Starts location tracking. Requires location permission.
     *
     * Idempotent — calling while already tracking is a no-op.
     *
     * @since 0.0.1
     */
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
                        _extendedLocation.value = ExtendedLocation(
                            latitude = extLoc.location.coordinates.latitude,
                            longitude = extLoc.location.coordinates.longitude,
                            accuracy = extLoc.location.coordinatesAccuracyMeters.toFloat(),
                            altitude = extLoc.altitude.altitudeMeters,
                            speed = extLoc.speed.speedMps.toFloat(),
                            bearing = extLoc.azimuth.azimuthDegrees.toFloat(),
                            timestamp = extLoc.timestampMs,
                        )
                    }
            }.onFailure { e ->
                _isTracking.value = false
                Logger.w("LocationManager") { "startTracking failed: ${e.message}" }
            }
        }
    }

    /**
     * Stops location tracking. Idempotent — no-op when not tracking.
     *
     * @since 0.0.1
     */
    override fun stopTracking() {
        if (!_isTracking.value) return
        scope.launch {
            runCatching {
                locationTracker.stopTracking()
                _isTracking.value = false
            }.onFailure { e ->
                Logger.w("LocationManager") { "stopTracking failed: ${e.message}" }
            }
        }
    }

    /**
     * Updates the externally-observed permission state.
     *
     * @param state New permission state, or `null` if unknown.
     * @since 0.0.1
     */
    fun updatePermissionState(state: LocationPermissionState?) {
        _permissionState.value = state
    }

    /**
     * Returns current location as [GeoPoint], or `null` if unavailable.
     *
     * @since 0.0.1
     */
    override fun getCurrentLocation(): GeoPoint? = _extendedLocation.value?.toGeoPoint()

    /**
     * Returns current location as [GeoPoint], or [defaultLocation] if unavailable.
     *
     * @since 0.0.1
     */
    override fun getCurrentLocationOrDefault(): GeoPoint = getCurrentLocation() ?: defaultLocation

    companion object {
        /**
         * Default fallback location: Tashkent, Uzbekistan.
         *
         * @since 0.0.1
         */
        val DEFAULT_LOCATION = GeoPoint(41.2995, 69.2401)
    }
}
