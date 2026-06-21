package uz.yalla.maps.config

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.LocationProvider

/** A [LocationProvider] that never reports a location; the default when the host supplies none. */
public object NoopLocationProvider : LocationProvider {
    override val currentLocation: StateFlow<GeoPoint?> = MutableStateFlow<GeoPoint?>(null).asStateFlow()

    override fun startTracking(): Unit = Unit

    override fun stopTracking(): Unit = Unit
}
