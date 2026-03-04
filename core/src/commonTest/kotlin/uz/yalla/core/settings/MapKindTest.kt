package uz.yalla.core.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class MapKindTest {
    @Test
    fun shouldReturnMapKindWhenIdMatches() {
        val mapKind = MapKind.from("libre")

        assertEquals(MapKind.Libre, mapKind)
    }

    @Test
    fun shouldNormalizeIdWhenFindingMapKind() {
        val mapKind = MapKind.from("  GOOGLE  ")

        assertEquals(MapKind.Google, mapKind)
    }

    @Test
    fun shouldReturnGoogleWhenIdIsUnknownOrNull() {
        assertEquals(MapKind.Google, MapKind.from("unknown"))
        assertEquals(MapKind.Google, MapKind.from(null))
    }
}
