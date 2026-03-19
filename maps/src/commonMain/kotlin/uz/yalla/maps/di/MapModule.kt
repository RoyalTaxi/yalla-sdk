package uz.yalla.maps.di

import org.koin.dsl.module
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.LiteMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapProvider
import uz.yalla.maps.api.StaticMap
import uz.yalla.maps.provider.SwitchingMapController
import uz.yalla.maps.provider.SwitchingMapProvider

/**
 * Koin module providing map-related dependencies.
 *
 * Registers [MapProvider], [LiteMap], [ExtendedMap], [StaticMap], and [MapController]
 * using [SwitchingMapProvider] and [SwitchingMapController] for runtime provider switching.
 *
 * @since 0.0.1
 */
val mapModule =
    module {
        single<MapProvider> { SwitchingMapProvider(get<MapDependencies>().interfacePreferences) }
        factory<LiteMap> { get<MapProvider>().createLiteMap() }
        factory<ExtendedMap> { get<MapProvider>().createExtendedMap() }
        factory<StaticMap> { get<MapProvider>().createStaticMap() }
        factory<MapController> { SwitchingMapController(get<MapDependencies>().interfacePreferences) }
    }
