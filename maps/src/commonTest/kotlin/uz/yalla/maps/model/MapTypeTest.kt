package uz.yalla.maps.model

import kotlin.test.Test
import kotlin.test.assertEquals

class MapTypeTest {
    @Test
    fun shouldHaveFiveTypes() {
        assertEquals(5, MapType.entries.size)
    }

    @Test
    fun shouldContainExpectedTypes() {
        val names = MapType.entries.map { it.name }
        assertEquals(listOf("NONE", "NORMAL", "SATELLITE", "HYBRID", "TERRAIN"), names)
    }
}
