package uz.yalla.maps.compose

import cocoapods.GoogleMaps.GMSPolyline
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal class PolylineNode(
    val polyline: GMSPolyline
) : MapNode {
    override fun onRemoved() {
        polyline.map = null
    }

    override fun onCleared() {
        polyline.map = null
    }
}
