package uz.yalla.maps.di

import org.koin.dsl.module
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.LiteMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapProvider
import uz.yalla.maps.api.StaticMap
import uz.yalla.maps.provider.SwitchingMapController
import uz.yalla.maps.provider.SwitchingMapProvider
import uz.yalla.maps.provider.google.GoogleMapProvider
import uz.yalla.maps.provider.libre.LibreMapProvider

/**
 * Koin module providing map-related dependencies.
 *
 * Registers [MapProvider], [LiteMap], [ExtendedMap], [StaticMap], and [MapController]
 * using [SwitchingMapProvider] and [SwitchingMapController] for runtime provider switching.
 *
 * The host application must register a [MapDependencies] implementation in the Koin
 * container before this module is loaded. [MapDependencies.scope] must be a
 * process-lifetime [kotlinx.coroutines.CoroutineScope] — see ADR-018.
 *
 * @since 0.0.1
 */
val mapModule =
    module {
        single<MapProvider> {
            val deps = get<MapDependencies>()
            SwitchingMapProvider(
                googleProvider = GoogleMapProvider(),
                libreProvider = LibreMapProvider(),
                interfacePreferences = deps.interfacePreferences,
                scope = deps.scope,
            )
        }
        factory<LiteMap> { get<MapProvider>().createLiteMap() }
        factory<ExtendedMap> { get<MapProvider>().createExtendedMap() }
        factory<StaticMap> { get<MapProvider>().createStaticMap() }
        factory<MapController> {
            val deps = get<MapDependencies>()
            SwitchingMapController(
                interfacePreferences = deps.interfacePreferences,
                scope = deps.scope,
            )
        }
    }
