package uz.yalla.maps.compose

import cocoapods.GoogleMaps.GMSMarker
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal class MarkerNode(
    val marker: GMSMarker
) : MapNode {
    override fun onRemoved() {
        marker.map = null
    }

    override fun onCleared() {
        marker.map = null
    }
}
