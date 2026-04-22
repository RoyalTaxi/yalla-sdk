package uz.yalla.maps.api.overlay

import uz.yalla.core.geo.GeoPoint

/**
 * Parameter contract for the user-location indicator overlay.
 *
 * Both `GoogleMaps` and `MapLibre` provider implementations of `LocationIndicator` share
 * these two parameters. The MapLibre implementation additionally requires a `CameraState`
 * to scale the accuracy circle with zoom — that parameter is provider-specific and is not
 * part of this shared contract.
 *
 * Callers construct a [LocationIndicatorConfig] and spread its fields into the provider's
 * `LocationIndicator` composable, ensuring the common parameters stay in sync across providers.
 *
 * Usage:
 * ```kotlin
 * val config = LocationIndicatorConfig(location = userGeoPoint, accuracyMeters = 15.0)
 * // Inside a Google map content lambda:
 * LocationIndicator(location = config.location, accuracyMeters = config.accuracyMeters)
 * // Inside a Libre map content lambda (note extra cameraState parameter):
 * LocationIndicator(
 *     location       = config.location,
 *     cameraState    = libreCamera,
 *     accuracyMeters = config.accuracyMeters,
 * )
 * ```
 *
 * @property location Current user location, or `null` to hide the indicator.
 * @property accuracyMeters GPS accuracy radius in metres. Defaults to 50.0 m, matching
 *   the provider default used by both Google and MapLibre implementations.
 * @since 0.0.1
 */
public data class LocationIndicatorConfig(
    val location: GeoPoint?,
    val accuracyMeters: Double = DEFAULT_ACCURACY_METERS,
) {
    public companion object {
        /** Default GPS accuracy radius in metres (50 m). Mirrors the provider-layer default. */
        public const val DEFAULT_ACCURACY_METERS: Double = 50.0
    }
}
