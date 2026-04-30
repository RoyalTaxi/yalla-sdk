package uz.yalla.maps.model

/**
 * Defines the shape drawn at the start or end of a [Polyline][uz.yalla.maps.compose.Polyline].
 *
 * Maps to platform-specific cap types on Android (Google Maps `Cap`) and iOS (`GMSPolyline`).
 */
sealed class Cap {
    /**
     * Flat cap with no extension beyond the endpoint.
     */
    data object Butt : Cap()

    /**
     * Semicircular cap extending half the stroke width beyond the endpoint.
     */
    data object Round : Cap()

    /**
     * Square cap extending half the stroke width beyond the endpoint.
     */
    data object Square : Cap()
}
