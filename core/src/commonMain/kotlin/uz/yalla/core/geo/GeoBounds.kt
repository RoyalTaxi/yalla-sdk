package uz.yalla.core.geo

import kotlinx.serialization.Serializable

/**
 * Represents a rectangular geographic area defined by southwest and northeast corners.
 *
 * @property southwest The southwest corner of the bounding box
 * @property northeast The northeast corner of the bounding box
 */
@Serializable
data class GeoBounds(
    val southwest: GeoPoint,
    val northeast: GeoPoint
) {
    /** The geographic center of this bounding box. */
    val center: GeoPoint
        get() =
            GeoPoint(
                lat = (southwest.lat + northeast.lat) / 2,
                lng = (southwest.lng + northeast.lng) / 2
            )

    /** Returns `true` if the given [point] lies within this bounding box. */
    fun contains(point: GeoPoint): Boolean =
        point.lat in southwest.lat..northeast.lat &&
            point.lng in southwest.lng..northeast.lng

    companion object {
        /** Creates a [GeoBounds] that encloses all given [points], or `null` if the list is empty. */
        fun fromPoints(points: List<GeoPoint>): GeoBounds? {
            if (points.isEmpty()) return null
            return GeoBounds(
                southwest = GeoPoint(points.minOf { it.lat }, points.minOf { it.lng }),
                northeast = GeoPoint(points.maxOf { it.lat }, points.maxOf { it.lng })
            )
        }
    }
}
