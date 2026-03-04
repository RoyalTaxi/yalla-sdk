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
    val currentLocation: Flow<GeoPoint?>

    /** Returns the last known location or null if unavailable. */
    fun getCurrentLocation(): GeoPoint?

    /** Returns the last known location or [GeoPoint.Zero] if unavailable. */
    fun getCurrentLocationOrDefault(): GeoPoint

    /** Starts receiving location updates via [currentLocation]. */
    fun startTracking()

    /** Stops location updates to conserve battery. */
    fun stopTracking()
}
