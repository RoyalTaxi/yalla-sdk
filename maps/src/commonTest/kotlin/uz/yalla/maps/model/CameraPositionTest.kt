package uz.yalla.maps.model

import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.api.model.CameraPosition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CameraPositionTest {
    @Test
    fun shouldStoreAllProperties() {
        val pos = CameraPosition(
            target = GeoPoint(41.0, 69.0),
            zoom = 15f,
            bearing = 90f,
            tilt = 30f,
        )
        assertEquals(GeoPoint(41.0, 69.0), pos.target)
        assertEquals(15f, pos.zoom)
        assertEquals(90f, pos.bearing)
        assertEquals(30f, pos.tilt)
    }

    @Test
    fun shouldDefaultBearingAndTiltToZero() {
        val pos = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 15f)
        assertEquals(0f, pos.bearing)
        assertEquals(0f, pos.tilt)
    }

    @Test
    fun shouldBeEqualWithSameValues() {
        val a = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 15f)
        val b = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 15f)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun shouldNotBeEqualWithDifferentTarget() {
        val a = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 15f)
        val b = CameraPosition(target = GeoPoint(42.0, 69.0), zoom = 15f)
        assertNotEquals(a, b)
    }

    @Test
    fun shouldNotBeEqualWithDifferentZoom() {
        val a = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 15f)
        val b = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 16f)
        assertNotEquals(a, b)
    }

    @Test
    fun shouldNotBeEqualWithDifferentBearing() {
        val a = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 15f, bearing = 0f)
        val b = CameraPosition(target = GeoPoint(41.0, 69.0), zoom = 15f, bearing = 90f)
        assertNotEquals(a, b)
    }

    @Test
    fun defaultShouldBeAtZeroWithZoom15() {
        val default = CameraPosition.DEFAULT
        assertEquals(GeoPoint.Zero, default.target)
        assertEquals(15f, default.zoom)
    }
}
