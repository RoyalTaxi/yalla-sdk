package uz.yalla.maps.api.overlay

import uz.yalla.core.geo.GeoPoint

/**
 * Parameter contract for the route polyline overlay.
 *
 * Both `GoogleMaps` and `MapLibre` provider implementations of `RouteLayer` accept exactly
 * these parameters. Callers construct a [RouteOverlayConfig] and pass it to the provider's
 * `RouteLayer` composable, ensuring parallel implementations stay in sync.
 *
 * Usage:
 * ```kotlin
 * val config = RouteOverlayConfig(route = myRoutePoints)
 * // Inside a Google or Libre map content lambda:
 * RouteLayer(config.route)
 * ```
 *
 * @property route Ordered list of coordinates defining the route path. Must contain at
 *   least two points for the polyline to render; fewer points are silently ignored.
 * @since 0.0.1
 */
public data class RouteOverlayConfig(
    val route: List<GeoPoint>,
)
