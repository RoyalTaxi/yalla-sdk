package uz.yalla.maps.compose

import cocoapods.GoogleMaps.GMSPolyline
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * Compose tree node for a [GMSPolyline] managed by [MapApplier].
 *
 * Detaches the polyline from the map when the node is removed or cleared.
 *
 * @property polyline The underlying iOS polyline overlay.
 * @since 0.0.1
 */
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
