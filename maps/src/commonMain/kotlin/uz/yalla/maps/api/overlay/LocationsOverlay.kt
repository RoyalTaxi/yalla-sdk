package uz.yalla.maps.api.overlay

import uz.yalla.core.geo.GeoPoint

/**
 * Parameter contract for the locations marker overlay.
 *
 * Both `GoogleMaps` and `MapLibre` provider implementations of `LocationsLayer` accept exactly
 * these parameters. Callers construct a [LocationsOverlayConfig] and pass it to the provider's
 * `LocationsLayer` composable, ensuring parallel implementations stay in sync.
 *
 * The overlay renders colored circle markers at each coordinate. The first point is treated as
 * the start marker, the last as the finish marker, and all intermediate points as waypoints.
 * Optional text labels are shown above start and finish markers.
 *
 * Usage:
 * ```kotlin
 * val config = LocationsOverlayConfig(
 *     locations = listOf(pickup, waypoint, dropoff),
 *     startLabel = "A",
 *     endLabel  = "B",
 * )
 * // Inside a Google or Libre map content lambda:
 * LocationsLayer(
 *     locations  = config.locations,
 *     startLabel = config.startLabel,
 *     endLabel   = config.endLabel,
 * )
 * ```
 *
 * @property locations Ordered list of geographic coordinates for the markers.
 * @property startLabel Optional text badge displayed above the first marker.
 * @property endLabel Optional text badge displayed above the last marker.
 * @since 0.0.1
 */
public data class LocationsOverlayConfig(
    val locations: List<GeoPoint>,
    val startLabel: String? = null,
    val endLabel: String? = null,
)
