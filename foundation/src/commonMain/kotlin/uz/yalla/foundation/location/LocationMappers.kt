package uz.yalla.foundation.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.Address
import uz.yalla.core.location.AddressOption
import uz.yalla.core.location.SavedAddress
import uz.yalla.core.order.Order

/**
 * Converts address option to [FoundLocation] with coordinates and place metadata.
 *
 * @since 0.0.1
 */
fun AddressOption.toFoundLocation() =
    FoundLocation(
        id = id,
        name = title,
        address = address,
        point = GeoPoint(lat, lng),
        placeKind = null
    )

/**
 * Converts saved address to [FoundLocation] with address details.
 *
 * @since 0.0.1
 */
fun SavedAddress.toFoundLocation() =
    FoundLocation(
        id = null,
        name = title,
        address = address,
        point = GeoPoint(lat = lat, lng = lng),
        placeKind = kind
    )

/**
 * Converts core [Address] to foundation [Location].
 *
 * @since 0.0.1
 */
fun Address.toLocation(point: GeoPoint = GeoPoint(lat = lat, lng = lng)) =
    Location(
        id = id,
        name = name,
        point = point
    )

/**
 * Converts order route point to [Location].
 *
 * @since 0.0.1
 */
fun Order.Taxi.Route.toLocation() =
    Location(
        id = index,
        name = fullAddress,
        point = GeoPoint(lat = coords.lat, lng = coords.lng)
    )

/**
 * Returns all route locations sorted by index.
 *
 * @since 0.0.1
 */
fun Order.sortedRouteLocations(): List<Location> {
    return taxi.routes
        .sortedBy { route -> route.index }
        .map { route -> route.toLocation() }
}
