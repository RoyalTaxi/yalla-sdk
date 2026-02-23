package uz.yalla.maps.api

import uz.yalla.core.kind.MapKind
import uz.yalla.maps.api.model.MapCapabilities
import uz.yalla.maps.api.model.MapStyle

interface MapProvider {
    val type: MapKind
    val capabilities: MapCapabilities
    val style: MapStyle

    fun createLiteMap(): LiteMap

    fun createExtendedMap(): ExtendedMap

    fun createController(): MapController
}
