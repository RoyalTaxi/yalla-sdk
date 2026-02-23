package uz.yalla.maps.compose

import cocoapods.GoogleMaps.GMSCircle
import kotlinx.cinterop.ExperimentalForeignApi

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
