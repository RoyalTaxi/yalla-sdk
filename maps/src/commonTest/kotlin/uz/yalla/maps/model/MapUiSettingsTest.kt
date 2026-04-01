package uz.yalla.maps.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MapUiSettingsTest {
    @Test
    fun shouldDefaultToAllEnabled() {
        val settings = MapUiSettings()
        assertTrue(settings.compassEnabled)
        assertTrue(settings.scrollGesturesEnabled)
        assertTrue(settings.zoomGesturesEnabled)
    }

    @Test
    fun shouldSupportCopy() {
        val original = MapUiSettings()
        val modified = original.copy(compassEnabled = false)
        assertFalse(modified.compassEnabled)
        assertTrue(original.compassEnabled)
    }

    @Test
    fun shouldImplementEquality() {
        assertEquals(MapUiSettings(), MapUiSettings())
    }

    @Test
    fun shouldDetectDifferences() {
        val a = MapUiSettings()
        val b = a.copy(zoomGesturesEnabled = false)
        assertNotEquals(a, b)
    }

    @Test
    fun shouldHaveConsistentHashCode() {
        val a = MapUiSettings()
        val b = MapUiSettings()
        assertEquals(a.hashCode(), b.hashCode())
    }
}
