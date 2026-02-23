package uz.yalla.maps.compose

import androidx.compose.runtime.AbstractApplier
import cocoapods.GoogleMaps.GMSMapView
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal class MapApplier(
    val mapView: GMSMapView
) : AbstractApplier<MapNode>(MapNodeRoot) {
    private val decorations = mutableListOf<MapNode>()

    override fun onClear() {
        mapView.clear()
        decorations.forEach { it.onCleared() }
        decorations.clear()
    }

    override fun insertBottomUp(
        index: Int,
        instance: MapNode
    ) {
        decorations.add(index, instance)
        instance.onAttached()
    }

    override fun insertTopDown(
        index: Int,
        instance: MapNode
    ) {}

    override fun move(
        from: Int,
        to: Int,
        count: Int
    ) {
        decorations.move(from, to, count)
    }

    override fun remove(
        index: Int,
        count: Int
    ) {
        repeat(count) { decorations[index + it].onRemoved() }
        decorations.remove(index, count)
    }
}
