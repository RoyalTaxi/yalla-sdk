package uz.yalla.maps.provider.google

import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.model.LatLng
import uz.yalla.maps.model.LatLngBounds

/**
 * Converts this [GeoPoint] to a compose-layer [LatLng].
 *
 * @return A [LatLng] with matching latitude and longitude.
 * @since 0.0.1
 */
internal fun GeoPoint.toLatLng(): LatLng = LatLng(latitude = lat, longitude = lng)

/**
 * Converts this compose-layer [LatLng] to a [GeoPoint].
 *
 * @return A [GeoPoint] with matching latitude and longitude.
 * @since 0.0.1
 */
internal fun LatLng.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)

/**
 * Computes a [LatLngBounds] enclosing all [GeoPoint]s in this list.
 *
 * @return A [LatLngBounds] spanning all points, or `null` if the list is empty.
 * @since 0.0.1
 */
internal fun List<GeoPoint>.toLatLngBounds(): LatLngBounds? {
    if (isEmpty()) return null
    val builder = LatLngBounds.Builder()
    forEach { builder.include(it.toLatLng()) }
    return builder.build()
}
