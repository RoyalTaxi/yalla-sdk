package uz.yalla.core.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.identity.PlaceId
import uz.yalla.core.identity.SavedId

public data class Place(
    val id: PlaceId,
    val savedId: SavedId? = null,
    val address: String,
    val name: String,
    val point: GeoPoint,
    val kind: PlaceKind,
    val parent: PlaceParent,
    val duration: Double? = null
)
