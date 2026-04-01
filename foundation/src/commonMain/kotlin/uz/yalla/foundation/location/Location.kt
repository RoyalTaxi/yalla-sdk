package uz.yalla.foundation.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.PlaceKind

/**
 * A location with optional name and coordinates.
 *
 * @property id Optional location identifier
 * @property name Optional human-readable location name
 * @property point Optional geographic coordinates
 * @since 0.0.1
 */
data class Location(
    val id: Int? = null,
    val name: String? = null,
    val point: GeoPoint? = null
)

/**
 * A found location from search results.
 *
 * @property id Optional location identifier
 * @property name Optional human-readable location name
 * @property address Optional formatted address string
 * @property point Optional geographic coordinates
 * @property placeKind Optional place type classification
 * @since 0.0.1
 */
data class FoundLocation(
    val id: Int? = null,
    val name: String? = null,
    val address: String? = null,
    val point: GeoPoint? = null,
    val placeKind: PlaceKind? = null
) {
    /**
     * Converts this [FoundLocation] to a [Location], dropping the [address] field.
     *
     * @return [Location] with [id], [name], and [point] carried over.
     * @since 0.0.1
     * @see Location
     */
    fun toLocation() =
        Location(
            id = this.id,
            name = this.name,
            point = this.point
        )
}
