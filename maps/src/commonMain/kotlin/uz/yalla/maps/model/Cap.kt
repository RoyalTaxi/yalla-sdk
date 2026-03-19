package uz.yalla.maps.model

/**
 * Defines the shape drawn at the start or end of a [Polyline][uz.yalla.maps.compose.Polyline].
 *
 * Maps to platform-specific cap types on Android (Google Maps `Cap`) and iOS (`GMSPolyline`).
 *
 * @since 0.0.1
 */
sealed class Cap {
    /**
     * Flat cap with no extension beyond the endpoint.
     *
     * @since 0.0.1
     */
    data object Butt : Cap()

    /**
     * Semicircular cap extending half the stroke width beyond the endpoint.
     *
     * @since 0.0.1
     */
    data object Round : Cap()

    /**
     * Square cap extending half the stroke width beyond the endpoint.
     *
     * @since 0.0.1
     */
    data object Square : Cap()
}
