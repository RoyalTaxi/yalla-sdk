package uz.yalla.core.contract.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint

interface PositionPreferences {
    val lastMapPosition: Flow<GeoPoint>
    fun setLastMapPosition(value: GeoPoint)
    val lastGpsPosition: Flow<GeoPoint>
    fun setLastGpsPosition(value: GeoPoint)
}
