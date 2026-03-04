package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint

/**
 * Contract for persisting the user's last known positions.
 *
 * Used to restore map camera position and show approximate location
 * before GPS lock is acquired. All properties are reactive [Flow]s.
 *
 * @since 0.0.1
 */
interface PositionPreferences {
    val lastMapPosition: Flow<GeoPoint>

    fun setLastMapPosition(value: GeoPoint)

    val lastGpsPosition: Flow<GeoPoint>

    fun setLastGpsPosition(value: GeoPoint)
}
