package uz.yalla.carto.model

import uz.yalla.core.geo.GeoPoint

public data class CartoMarker(
    val id: String,
    val point: GeoPoint,
    val iconKey: String? = null,
    val anchor: MapAnchor = MapAnchor.BOTTOM,
    val rotation: Float = 0f,
    val flat: Boolean = false,
    val routeHeading: Float? = null,
    val zIndex: Float = 0f,
    val zBand: ZBand = ZBand.MARKERS
)
