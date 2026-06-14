package uz.yalla.maps.api.ios

import uz.yalla.maps.api.MapController
import uz.yalla.maps.config.MapFactory

public interface IosMapRendererFactory {
    public fun createGoogleRenderer(): IosMapRenderer

    public fun createLibreRenderer(): IosMapRenderer
}

public fun iosMapFactoryOf(rendererFactory: IosMapRendererFactory): MapFactory = object : MapFactory {
    override fun createGoogleController(): MapController =
        IosMapControllerWrapper(rendererFactory.createGoogleRenderer())

    override fun createLibreController(): MapController =
        IosMapControllerWrapper(rendererFactory.createLibreRenderer())
}
