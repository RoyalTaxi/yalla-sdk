package uz.yalla.core.contract

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint

interface LastLocationProvider {
    val lastLocation: Flow<GeoPoint?>

    fun setLastLocation(point: GeoPoint)
}
