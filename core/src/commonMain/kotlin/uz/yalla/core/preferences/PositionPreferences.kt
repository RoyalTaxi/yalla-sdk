package uz.yalla.core.preferences

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint

public interface PositionPreferences {
    public val lastMapPosition: Flow<GeoPoint>

    public fun setLastMapPosition(value: GeoPoint)

    public val lastGpsPosition: Flow<GeoPoint>

    public fun setLastGpsPosition(value: GeoPoint)
}
