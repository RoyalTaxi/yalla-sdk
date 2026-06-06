package uz.yalla.core.location

import kotlinx.coroutines.flow.StateFlow
import uz.yalla.core.geo.GeoPoint

interface LocationProvider {
    val currentLocation: StateFlow<GeoPoint?>
    fun startTracking()
    fun stopTracking()
}
