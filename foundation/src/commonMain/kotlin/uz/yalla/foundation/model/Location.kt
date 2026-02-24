package uz.yalla.foundation.model

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.geo.PlaceKind

/**
 * A location with optional name and coordinates.
 */
data class Location(
    val id: Int? = null,
    val name: String? = null,
    val point: GeoPoint? = null
)

/**
 * A found location from search results.
 */
data class FoundLocation(
    val id: Int? = null,
    val name: String? = null,
    val address: String? = null,
    val point: GeoPoint? = null,
    val placeKind: PlaceKind? = null
) {
    fun toLocation() =
        Location(
            id = this.id,
            name = this.name,
            point = this.point
        )
}
