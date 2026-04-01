package uz.yalla.maps.model

import uz.yalla.maps.api.model.MapCapabilities
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapCapabilitiesTest {
    @Test
    fun librePresetShouldSupportOffline() {
        assertTrue(MapCapabilities.LIBRE.supportsOffline)
    }

    @Test
    fun librePresetShouldSupportCustomStyles() {
        assertTrue(MapCapabilities.LIBRE.supportsCustomStyles)
    }

    @Test
    fun librePresetShouldNotSupportTraffic() {
        assertFalse(MapCapabilities.LIBRE.supportsTraffic)
    }

    @Test
    fun librePresetShouldNotSupportStreetView() {
        assertFalse(MapCapabilities.LIBRE.supportsStreetView)
    }

    @Test
    fun googlePresetShouldSupportTraffic() {
        assertTrue(MapCapabilities.GOOGLE.supportsTraffic)
    }

    @Test
    fun googlePresetShouldNotSupportOffline() {
        assertFalse(MapCapabilities.GOOGLE.supportsOffline)
    }

    @Test
    fun googlePresetShouldNotSupportCustomStyles() {
        assertFalse(MapCapabilities.GOOGLE.supportsCustomStyles)
    }

    @Test
    fun bothPresetsShouldHaveSameZoomRange() {
        assertEquals(MapCapabilities.LIBRE.maxZoom, MapCapabilities.GOOGLE.maxZoom)
        assertEquals(MapCapabilities.LIBRE.minZoom, MapCapabilities.GOOGLE.minZoom)
    }

    @Test
    fun defaultsShouldBeConservative() {
        val defaults = MapCapabilities()
        assertFalse(defaults.supportsOffline)
        assertFalse(defaults.supports3D)
        assertFalse(defaults.supportsTraffic)
        assertFalse(defaults.supportsStreetView)
        assertTrue(defaults.supportsCustomStyles)
    }
}
