package uz.yalla.maps.config

import uz.yalla.maps.api.MapController

public interface MapFactory {
    public fun createGoogleController(): MapController

    public fun createLibreController(): MapController
}
