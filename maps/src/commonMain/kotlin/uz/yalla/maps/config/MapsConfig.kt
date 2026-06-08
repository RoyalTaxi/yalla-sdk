package uz.yalla.maps.config

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import uz.yalla.core.location.LocationProvider
import uz.yalla.core.settings.MapKind
import uz.yalla.core.settings.ThemeKind
import uz.yalla.maps.api.MapsTelemetry

class MapsConfig private constructor(
    val factory: MapFactory,
    val locationProvider: LocationProvider,
    val themePreference: Flow<ThemeKind>,
    val mapKindPreference: Flow<MapKind>,
    val scope: CoroutineScope,
    val telemetry: MapsTelemetry?
) {
    class Builder {
        var factory: MapFactory? = null

        var locationProvider: LocationProvider? = null

        var themePreference: Flow<ThemeKind>? = null

        var mapKindPreference: Flow<MapKind>? = null

        var scope: CoroutineScope? = null

        var telemetry: MapsTelemetry? = null

        fun build() = MapsConfig(
            factory = requireNotNull(factory) { "MapsConfig.factory required" },
            locationProvider = requireNotNull(locationProvider) { "MapsConfig.locationProvider required" },
            themePreference = requireNotNull(themePreference) { "MapsConfig.themePreference required" },
            mapKindPreference = requireNotNull(mapKindPreference) { "MapsConfig.mapKindPreference required" },
            scope = requireNotNull(scope) { "MapsConfig.scope required" },
            telemetry = telemetry
        )
    }
}
