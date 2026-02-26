package uz.yalla.core.contract.location

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint

interface LocationProvider {
    val currentLocation: Flow<GeoPoint?>

    fun getCurrentLocation(): GeoPoint?

    fun getCurrentLocationOrDefault(): GeoPoint

    fun startTracking()

    fun stopTracking()
}
