package uz.yalla.maps.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.location.LocationProvider
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind

public class MapsConfig private constructor(
    public val factory: MapFactory,
    public val locationProvider: LocationProvider,
    public val themePreference: Flow<ThemeKind>,
    public val mapKindPreference: Flow<MapKind>,
    public val scope: CoroutineScope
) {
    public class Builder {
        public var factory: MapFactory? = null

        public var locationProvider: LocationProvider? = null

        public var themePreference: Flow<ThemeKind>? = null

        public var mapKindPreference: Flow<MapKind>? = null

        public var scope: CoroutineScope? = null

        public fun build(): MapsConfig =
            MapsConfig(
                factory = requireNotNull(factory) { "MapsConfig.factory required" },
                locationProvider = requireNotNull(locationProvider) { "MapsConfig.locationProvider required" },
                themePreference = requireNotNull(themePreference) { "MapsConfig.themePreference required" },
                mapKindPreference = requireNotNull(mapKindPreference) { "MapsConfig.mapKindPreference required" },
                scope = requireNotNull(scope) { "MapsConfig.scope required" }
            )
    }
}
