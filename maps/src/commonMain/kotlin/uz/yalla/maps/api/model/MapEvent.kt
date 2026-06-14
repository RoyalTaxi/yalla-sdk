package uz.yalla.maps.api.model

import uz.yalla.core.geo.GeoPoint

public sealed class MapEvent {
    public data class MarkerTapped(val id: String) : MapEvent()

    public data class MapTapped(val point: GeoPoint) : MapEvent()

    public data class MapLongPressed(val point: GeoPoint) : MapEvent()
}
