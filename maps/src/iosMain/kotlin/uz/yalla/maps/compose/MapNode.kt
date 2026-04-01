package uz.yalla.maps.compose

/**
 * Lifecycle-aware node in the iOS Google Maps composition tree.
 *
 * Implementations detach their native overlay (marker, polyline, circle) from the
 * `GMSMapView` in [onRemoved] and [onCleared].
 *
 * @since 0.0.1
 * @see MapApplier
 */
internal interface MapNode {
    /** Called when the node is attached to the composition tree. */
    fun onAttached() {}

    /** Called when the node is removed from the composition tree. */
    fun onRemoved() {}

    /** Called when the composition tree is cleared. */
    fun onCleared() {}
}

/**
 * Sentinel root node for [MapApplier].
 *
 * @since 0.0.1
 */
internal object MapNodeRoot : MapNode
