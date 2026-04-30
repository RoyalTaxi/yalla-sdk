package uz.yalla.maps.compose

import cocoapods.GoogleMaps.GMSCircle
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * Compose tree node for a [GMSCircle] managed by [MapApplier].
 *
 * Detaches the circle from the map when the node is removed or cleared.
 */
@OptIn(ExperimentalForeignApi::class)
internal class CircleNode(
    val circle: GMSCircle
) : MapNode {
    override fun onRemoved() {
        circle.map = null
    }

    override fun onCleared() {
        circle.map = null
    }
}
