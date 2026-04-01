package uz.yalla.maps.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LatLngTest {
    @Test
    fun shouldStoreLatitudeAndLongitude() {
        val point = LatLng(41.3111, 69.2797)
        assertEquals(41.3111, point.latitude)
        assertEquals(69.2797, point.longitude)
    }

    @Test
    fun shouldBeValidForNormalCoordinates() {
        assertTrue(LatLng(41.3111, 69.2797).isValid())
    }

    @Test
    fun shouldBeValidAtBoundaryValues() {
        assertTrue(LatLng(90.0, 180.0).isValid())
        assertTrue(LatLng(-90.0, -180.0).isValid())
        assertTrue(LatLng(0.0, 0.0).isValid())
    }

    @Test
    fun shouldBeInvalidWhenLatitudeOutOfRange() {
        assertFalse(LatLng(91.0, 69.0).isValid())
        assertFalse(LatLng(-91.0, 69.0).isValid())
    }

    @Test
    fun shouldBeInvalidWhenLongitudeOutOfRange() {
        assertFalse(LatLng(41.0, 181.0).isValid())
        assertFalse(LatLng(41.0, -181.0).isValid())
    }

    @Test
    fun shouldImplementDataClassEquality() {
        assertEquals(LatLng(41.0, 69.0), LatLng(41.0, 69.0))
    }
}
