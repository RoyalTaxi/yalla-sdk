package uz.yalla.foundation.location

import androidx.compose.runtime.Immutable
import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.PlaceKind

@Immutable
data class Location(
    val id: Int? = null,
    val name: String? = null,
    val point: GeoPoint? = null
)

/** A found location from search results. */
@Immutable
data class FoundLocation(
    val id: Int? = null,
    val name: String? = null,
    val address: String? = null,
    val point: GeoPoint? = null,
    val placeKind: PlaceKind? = null
) {
    /**
     * Converts this [FoundLocation] to a [Location], dropping the [address] field.
     */
    fun toLocation() =
        Location(
            id = this.id,
            name = this.name,
            point = this.point
        )
}
