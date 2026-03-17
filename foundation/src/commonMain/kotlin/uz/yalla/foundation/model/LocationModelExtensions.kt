package uz.yalla.foundation.model

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.location.Address
import uz.yalla.core.location.AddressOption
import uz.yalla.core.location.SavedAddress
import uz.yalla.core.order.Order

fun AddressOption.toFoundLocation() =
    FoundLocation(
        id = id,
        name = title,
        address = address,
        point = GeoPoint(lat, lng),
        placeKind = null
    )

fun SavedAddress.toFoundLocation() =
    FoundLocation(
        id = null,
        name = title,
        address = address,
        point = GeoPoint(lat = lat, lng = lng),
        placeKind = kind
    )

fun Address.toLocation(point: GeoPoint = GeoPoint(lat = lat, lng = lng)) =
    Location(
        id = id,
        name = name,
        point = point
    )

fun Order.Taxi.Route.toLocation() =
    Location(
        id = index,
        name = fullAddress,
        point = GeoPoint(lat = coords.lat, lng = coords.lng)
    )

fun Order.sortedRouteLocations(): List<Location> {
    return taxi.routes
        .sortedBy { route -> route.index }
        .map { route -> route.toLocation() }
}
