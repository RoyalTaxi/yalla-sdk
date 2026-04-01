package uz.yalla.maps.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapPropertiesTest {
    @Test
    fun shouldDefaultToNormalMapType() {
        val props = MapProperties()
        assertEquals(MapType.NORMAL, props.mapType)
    }

    @Test
    fun shouldDefaultBuildingsEnabled() {
        assertTrue(MapProperties().isBuildingEnabled)
    }

    @Test
    fun shouldDefaultTrafficDisabled() {
        assertFalse(MapProperties().isTrafficEnabled)
    }

    @Test
    fun shouldSupportCopy() {
        val original = MapProperties()
        val modified = original.copy(isTrafficEnabled = true)
        assertTrue(modified.isTrafficEnabled)
        assertFalse(original.isTrafficEnabled)
    }
}
