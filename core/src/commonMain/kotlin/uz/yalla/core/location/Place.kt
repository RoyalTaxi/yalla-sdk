package uz.yalla.core.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.identity.PlaceId

public data class Place(
    val id: PlaceId?,
    val name: String,
    val address: Address,
    val point: GeoPoint,
    val kind: PlaceKind,
    val duration: Double? = null
)
