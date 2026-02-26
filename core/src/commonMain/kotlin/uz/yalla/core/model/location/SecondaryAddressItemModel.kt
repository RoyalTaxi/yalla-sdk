package uz.yalla.core.model.location

import uz.yalla.core.geo.PlaceKind

data class SecondaryAddressItemModel(
    val distance: Double,
    val duration: Double,
    val lat: Double,
    val lng: Double,
    val addressName: String,
    val name: String,
    val type: PlaceKind,
    val parent: Parent
) {
    data class Parent(val name: String?)
}
