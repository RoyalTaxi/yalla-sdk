package uz.yalla.foundation.util

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.model.ShowOrderModel
import uz.yalla.core.model.location.PlaceNameModel
import uz.yalla.core.model.location.SearchForAddressItemModel
import uz.yalla.core.model.location.SecondaryAddressItemModel
import uz.yalla.foundation.model.FoundLocation
import uz.yalla.foundation.model.Location

fun SearchForAddressItemModel.toFoundLocation() =
    FoundLocation(
        id = addressId,
        name = name,
        address = addressName,
        point = GeoPoint(lat, lng),
        placeKind = null
    )

fun SecondaryAddressItemModel.toFoundLocation() =
    FoundLocation(
        id = null,
        name = name,
        address = addressName,
        point = GeoPoint(lat = lat, lng = lng),
        placeKind = type
    )

fun PlaceNameModel.toLocation(point: GeoPoint = GeoPoint(lat = lat, lng = lng)) =
    Location(
        id = id,
        name = displayName,
        point = point
    )

fun ShowOrderModel.Taxi.Route.toLocation() =
    Location(
        id = index,
        name = fullAddress,
        point = GeoPoint(lat = coords.lat, lng = coords.lng)
    )

fun ShowOrderModel.sortedRouteLocations(): List<Location> {
    return taxi.routes
        .sortedBy { route -> route.index }
        .map { route -> route.toLocation() }
}
