package uz.yalla.core.model

import uz.yalla.core.geo.PlaceKind

data class SearchableAddress(
    val addressId: Int?,
    val addressName: String,
    val distance: Double?,
    val type: PlaceKind,
    val lat: Double,
    val lng: Double,
    val name: String
)
