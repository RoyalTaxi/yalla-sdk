package uz.yalla.maps.config

import kotlinx.coroutines.flow.Flow
import uz.yalla.core.location.LocationProvider
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind

/**
 * Install-time configuration for the maps SDK, built via [Builder] and registered with
 * [YallaMaps.install]. Supplies the backend [factory], the live [locationProvider] (user-location
 * source), and the [themePreference]/[mapKindPreference] flows the SDK observes.
 *
 * @param factory creates the per-backend controllers.
 * @param locationProvider the live user-location source.
 * @param themePreference the app's light/dark/system theme preference.
 * @param mapKindPreference the app's Google/MapLibre preference.
 */
public class MapsConfig private constructor(
    public val factory: MapFactory,
    public val locationProvider: LocationProvider,
    public val themePreference: Flow<ThemeKind>,
    public val mapKindPreference: Flow<MapKind>
) {
    /** Builder for [MapsConfig]; all fields are required. */
    public class Builder {
        public var factory: MapFactory? = null

        public var locationProvider: LocationProvider? = null

        public var themePreference: Flow<ThemeKind>? = null

        public var mapKindPreference: Flow<MapKind>? = null

        public fun build(): MapsConfig =
            MapsConfig(
                factory = requireNotNull(factory) { "MapsConfig.factory required" },
                locationProvider = requireNotNull(locationProvider) { "MapsConfig.locationProvider required" },
                themePreference = requireNotNull(themePreference) { "MapsConfig.themePreference required" },
                mapKindPreference = requireNotNull(mapKindPreference) { "MapsConfig.mapKindPreference required" }
            )
    }
}
