package uz.yalla.core.location

import kotlin.test.Test
import kotlin.test.assertEquals

class PlaceKindTest {
    @Test
    fun shouldReturnPlaceKindWhenIdMatches() {
        val placeKind = PlaceKind.from("home")

        assertEquals(PlaceKind.Home, placeKind)
    }

    @Test
    fun shouldNormalizeIdWhenFindingPlaceKind() {
        val placeKind = PlaceKind.from("  WORK  ")

        assertEquals(PlaceKind.Work, placeKind)
    }

    @Test
    fun shouldReturnOtherWhenIdIsUnknownOrNull() {
        assertEquals(PlaceKind.Other, PlaceKind.from("unknown"))
        assertEquals(PlaceKind.Other, PlaceKind.from(null))
    }
}
