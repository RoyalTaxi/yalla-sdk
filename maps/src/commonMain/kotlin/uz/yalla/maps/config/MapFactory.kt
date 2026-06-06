package uz.yalla.maps.config

import uz.yalla.maps.api.MapProvider

interface MapFactory {
    fun createGoogleProvider(): MapProvider

    fun createLibreProvider(): MapProvider
}
