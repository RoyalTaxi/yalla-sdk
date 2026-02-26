package uz.yalla.foundation.location

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.contract.location.LocationProvider as LocationProviderContract

class LocationProviderAdapter(
    private val locationManager: LocationManager
) : LocationProviderContract {
    override val currentLocation: Flow<GeoPoint?>
        get() = locationManager.currentLocation

    override fun getCurrentLocation(): GeoPoint? = locationManager.getCurrentLocation()

    override fun getCurrentLocationOrDefault(): GeoPoint = locationManager.getCurrentLocationOrDefault()

    override fun startTracking() {
        locationManager.startTracking()
    }

    override fun stopTracking() {
        locationManager.stopTracking()
    }
}
