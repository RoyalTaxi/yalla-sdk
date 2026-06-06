package uz.yalla.core.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.identity.PlaceId

data class Place(
    val id: PlaceId?,
    val name: String,
    val address: Address,
    val point: GeoPoint,
    val kind: PlaceKind
)
