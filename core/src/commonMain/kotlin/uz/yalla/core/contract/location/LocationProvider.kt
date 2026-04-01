package uz.yalla.core.contract.location

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint

/**
 * Contract for GPS location tracking.
 *
 * Provides reactive location updates and one-shot location queries.
 * Implemented per-platform (Android/iOS) in the platform module.
 *
 * ## Usage
 * ```kotlin
 * locationProvider.currentLocation
 *     .filterNotNull()
 *     .collect { point -> updateMapCamera(point) }
 * ```
 *
 * @since 0.0.1
 */
interface LocationProvider {

    /**
     * Reactive stream of the device's current GPS position.
     *
     * Emits `null` before the first fix is acquired or if location
     * permissions are denied. Emits new values as the device moves
     * while [tracking is active][startTracking].
     *
     * @see startTracking
     * @see stopTracking
     */
    val currentLocation: Flow<GeoPoint?>

    /**
     * Returns the last known location or `null` if unavailable.
     *
     * This is a one-shot synchronous read; it does **not** request a fresh fix.
     *
     * @return The last known [GeoPoint], or `null` if no location has been received
     */
    fun getCurrentLocation(): GeoPoint?

    /**
     * Returns the last known location or [GeoPoint.Zero] if unavailable.
     *
     * Convenience overload that guarantees a non-null result.
     *
     * @return The last known [GeoPoint], or [GeoPoint.Zero] as fallback
     * @see getCurrentLocation
     */
    fun getCurrentLocationOrDefault(): GeoPoint

    /**
     * Starts receiving location updates via [currentLocation].
     *
     * Must be called after location permissions are granted. Updates continue
     * until [stopTracking] is called.
     *
     * @see stopTracking
     */
    fun startTracking()

    /**
     * Stops location updates to conserve battery.
     *
     * The [currentLocation] flow retains its last emitted value but
     * will not emit new positions until [startTracking] is called again.
     *
     * @see startTracking
     */
    fun stopTracking()
}
