package uz.yalla.maps.config

import uz.yalla.maps.api.MapController

interface MapFactory {
    fun createGoogleController(): MapController

    fun createLibreController(): MapController
}
