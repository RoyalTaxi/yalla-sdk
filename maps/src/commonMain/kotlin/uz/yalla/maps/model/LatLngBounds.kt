package uz.yalla.maps.model

/**
 * Axis-aligned geographic bounding rectangle defined by its southwest and northeast corners.
 *
 * Used by [CameraPositionState][uz.yalla.maps.compose.CameraPositionState] to animate
 * the camera to fit a set of coordinates within the viewport.
 *
 * @property southwest The south-west corner of the bounding box.
 * @property northeast The north-east corner of the bounding box.
 * @since 0.0.1
 */
data class LatLngBounds(
    val southwest: LatLng,
    val northeast: LatLng,
) {
    /**
     * The geographic midpoint of this bounding rectangle.
     *
     * @since 0.0.1
     */
    val center: LatLng
        get() =
            LatLng(
                latitude = (southwest.latitude + northeast.latitude) / 2,
                longitude = (southwest.longitude + northeast.longitude) / 2,
            )

    /**
     * Incrementally builds a [LatLngBounds] by including geographic points.
     *
     * @since 0.0.1
     */
    class Builder {
        private var southWestLat = Double.MAX_VALUE
        private var southWestLng = Double.MAX_VALUE
        private var northEastLat = -Double.MAX_VALUE
        private var northEastLng = -Double.MAX_VALUE

        /**
         * Expands the bounds to include the given [point].
         *
         * @param point The coordinate to include.
         * @return This builder for chaining.
         * @since 0.0.1
         */
        fun include(point: LatLng): Builder {
            southWestLat = minOf(southWestLat, point.latitude)
            southWestLng = minOf(southWestLng, point.longitude)
            northEastLat = maxOf(northEastLat, point.latitude)
            northEastLng = maxOf(northEastLng, point.longitude)
            return this
        }

        /**
         * Builds the [LatLngBounds] from all included points.
         *
         * @return The computed bounding rectangle.
         * @throws IllegalArgumentException if no points were included.
         * @since 0.0.1
         */
        fun build(): LatLngBounds {
            require(southWestLat != Double.MAX_VALUE) { "No points included" }
            return LatLngBounds(
                southwest = LatLng(southWestLat, southWestLng),
                northeast = LatLng(northEastLat, northEastLng),
            )
        }
    }
}
