package uz.yalla.maps.di

import org.koin.dsl.module
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.LiteMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapProvider
import uz.yalla.maps.provider.SwitchingMapController
import uz.yalla.maps.provider.SwitchingMapProvider

val mapModule =
    module {
        single<MapProvider> { SwitchingMapProvider(get<MapDependencies>().mapPreferences) }
        factory<LiteMap> { get<MapProvider>().createLiteMap() }
        factory<ExtendedMap> { get<MapProvider>().createExtendedMap() }
        factory<MapController> { SwitchingMapController(get<MapDependencies>().mapPreferences) }
    }
