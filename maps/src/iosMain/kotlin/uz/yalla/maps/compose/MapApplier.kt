@file:Suppress(
    "EmptyFunctionBlock", // MapApplier overrides use move/remove/etc.; insertTopDown is intentionally a no-op
)

package uz.yalla.maps.compose

import androidx.compose.runtime.AbstractApplier
import cocoapods.GoogleMaps.GMSMapView
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * Compose [AbstractApplier] for the iOS Google Maps composition tree.
 *
 * Manages the lifecycle of [MapNode] children (markers, polylines, circles) by
 * attaching them to and removing them from the underlying [GMSMapView].
 *
 * @property mapView The iOS Google Maps view that hosts the overlays.
 * @since 0.0.1
 */
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
