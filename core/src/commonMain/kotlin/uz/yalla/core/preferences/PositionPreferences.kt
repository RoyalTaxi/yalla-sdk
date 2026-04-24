package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint

/**
 * Contract for persisting the user's last known positions.
 *
 * Used to restore map camera position and show approximate location
 * before GPS lock is acquired. All properties are reactive [Flow]s.
 *
 * Defaults to [GeoPoint.Zero] when no position has been stored yet.
 *
 * @see uz.yalla.core.contract.location.LocationProvider
 * @since 0.0.1
 */
interface PositionPreferences {

    /**
     * Last camera center position on the map.
     *
     * Restored on app launch to avoid the map starting at [GeoPoint.Zero].
     */
    val lastMapPosition: Flow<GeoPoint>

    /**
     * Persists the current map camera center position.
     *
     * @param value The [GeoPoint] representing the map center
     */
    fun setLastMapPosition(value: GeoPoint)

    /**
     * Last known GPS position from the device location provider.
     *
     * Used as a fallback when GPS is not yet available.
     */
    val lastGpsPosition: Flow<GeoPoint>

    /**
     * Persists the latest GPS position.
     *
     * @param value The [GeoPoint] from the location provider
     */
    fun setLastGpsPosition(value: GeoPoint)
}
