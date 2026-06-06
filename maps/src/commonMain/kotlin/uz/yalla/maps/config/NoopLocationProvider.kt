package uz.yalla.maps.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.LocationProvider

object NoopLocationProvider : LocationProvider {
    override val currentLocation: StateFlow<GeoPoint?> = MutableStateFlow<GeoPoint?>(null).asStateFlow()

    override fun startTracking() = Unit

    override fun stopTracking() = Unit
}
