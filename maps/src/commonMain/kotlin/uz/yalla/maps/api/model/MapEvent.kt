package uz.yalla.maps.api.model

import uz.yalla.core.geo.GeoPoint

sealed class MapEvent {
    data class MarkerTapped(val id: String) : MapEvent()

    data class MapTapped(val point: GeoPoint) : MapEvent()

    data class MapLongPressed(val point: GeoPoint) : MapEvent()
}
