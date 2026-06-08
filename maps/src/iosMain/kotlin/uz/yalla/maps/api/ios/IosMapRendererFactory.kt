package uz.yalla.maps.api.ios

import uz.yalla.maps.api.MapController
import uz.yalla.maps.config.MapFactory

interface IosMapRendererFactory {
    fun createGoogleRenderer(): IosMapRenderer

    fun createLibreRenderer(): IosMapRenderer
}

fun iosMapFactoryOf(rendererFactory: IosMapRendererFactory): MapFactory = object : MapFactory {
    override fun createGoogleController(): MapController =
        IosMapControllerWrapper(rendererFactory.createGoogleRenderer())

    override fun createLibreController(): MapController =
        IosMapControllerWrapper(rendererFactory.createLibreRenderer())
}
