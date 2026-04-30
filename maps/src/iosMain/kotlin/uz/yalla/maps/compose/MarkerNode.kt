package uz.yalla.maps.compose

import cocoapods.GoogleMaps.GMSMarker
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * Compose tree node for a [GMSMarker] managed by [MapApplier].
 *
 * Detaches the marker from the map when the node is removed or cleared.
 */
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
