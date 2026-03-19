package uz.yalla.maps.provider.google

import uz.yalla.core.settings.MapKind
import uz.yalla.maps.api.ExtendedMap
import uz.yalla.maps.api.LiteMap
import uz.yalla.maps.api.MapController
import uz.yalla.maps.api.MapProvider
import uz.yalla.maps.api.StaticMap
import uz.yalla.maps.api.model.MapCapabilities
import uz.yalla.maps.api.model.MapStyle

/**
 * [MapProvider] implementation backed by Google Maps SDK.
 *
 * Exposes [MapCapabilities.GOOGLE] capabilities and creates Google-specific
 * map composables and controllers.
 *
 * @since 0.0.1
 */
class GoogleMapProvider : MapProvider {
    override val type = MapKind.Google
    override val capabilities = MapCapabilities.GOOGLE
    override val style = MapStyle.GOOGLE

    override fun createLiteMap(): LiteMap = GoogleLiteMap()

    override fun createExtendedMap(): ExtendedMap = GoogleExtendedMap()

    override fun createStaticMap(): StaticMap = GoogleStaticMap()

    override fun createController(): MapController = GoogleMapController()
}
