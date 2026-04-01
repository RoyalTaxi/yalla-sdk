package uz.yalla.foundation.location

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.Address
import uz.yalla.core.location.AddressOption
import uz.yalla.core.location.SavedAddress
import uz.yalla.core.order.Order

/**
 * Converts an [AddressOption] to a [FoundLocation] with coordinates and place metadata.
 *
 * @return [FoundLocation] populated from this address option's fields.
 * @since 0.0.1
 * @see FoundLocation
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
 * Converts a [SavedAddress] to a [FoundLocation] with address details and place kind.
 *
 * @return [FoundLocation] populated from this saved address's fields.
 * @since 0.0.1
 * @see FoundLocation
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
 * Converts a core [Address] to a foundation [Location].
 *
 * @param point Geographic coordinates to use. Defaults to the address's own lat/lng.
 * @return [Location] populated from this address's fields.
 * @since 0.0.1
 * @see Location
 */
fun Address.toLocation(point: GeoPoint = GeoPoint(lat = lat, lng = lng)) =
    Location(
        id = id,
        name = name,
        point = point
    )

/**
 * Converts an order route point to a [Location].
 *
 * Uses the route's [index][Order.Taxi.Route.index] as the location id and
 * [fullAddress][Order.Taxi.Route.fullAddress] as the name.
 *
 * @return [Location] populated from this route point's fields.
 * @since 0.0.1
 * @see Location
 */
fun Order.Taxi.Route.toLocation() =
    Location(
        id = index,
        name = fullAddress,
        point = GeoPoint(lat = coords.lat, lng = coords.lng)
    )

/**
 * Returns all route locations from this order, sorted by [route index][Order.Taxi.Route.index].
 *
 * @return List of [Location] objects ordered by their route index.
 * @since 0.0.1
 * @see Order.Taxi.Route.toLocation
 */
fun Order.sortedRouteLocations(): List<Location> {
    return taxi.routes
        .sortedBy { route -> route.index }
        .map { route -> route.toLocation() }
}
