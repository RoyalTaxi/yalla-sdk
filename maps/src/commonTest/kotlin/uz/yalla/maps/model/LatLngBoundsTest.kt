package uz.yalla.maps.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LatLngBoundsTest {
    @Test
    fun shouldComputeCenter() {
        val bounds = LatLngBounds(
            southwest = LatLng(40.0, 68.0),
            northeast = LatLng(42.0, 70.0),
        )
        assertEquals(41.0, bounds.center.latitude)
        assertEquals(69.0, bounds.center.longitude)
    }

    @Test
    fun shouldComputeCenterForSinglePoint() {
        val bounds = LatLngBounds(
            southwest = LatLng(41.0, 69.0),
            northeast = LatLng(41.0, 69.0),
        )
        assertEquals(41.0, bounds.center.latitude)
        assertEquals(69.0, bounds.center.longitude)
    }

    @Test
    fun shouldBuildFromMultiplePoints() {
        val bounds = LatLngBounds.Builder()
            .include(LatLng(40.0, 68.0))
            .include(LatLng(42.0, 70.0))
            .include(LatLng(41.0, 69.0))
            .build()

        assertEquals(40.0, bounds.southwest.latitude)
        assertEquals(68.0, bounds.southwest.longitude)
        assertEquals(42.0, bounds.northeast.latitude)
        assertEquals(70.0, bounds.northeast.longitude)
    }

    @Test
    fun shouldBuildFromSinglePoint() {
        val bounds = LatLngBounds.Builder()
            .include(LatLng(41.0, 69.0))
            .build()

        assertEquals(41.0, bounds.southwest.latitude)
        assertEquals(41.0, bounds.northeast.latitude)
    }

    @Test
    fun shouldThrowWhenBuildingWithNoPoints() {
        assertFailsWith<IllegalArgumentException> {
            LatLngBounds.Builder().build()
        }
    }

    @Test
    fun shouldSupportChainingIncludes() {
        val builder = LatLngBounds.Builder()
        val returned = builder.include(LatLng(41.0, 69.0))
        assertEquals(builder, returned)
    }
}
