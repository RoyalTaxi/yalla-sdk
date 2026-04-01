package uz.yalla.maps.util

import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapExtensionsTest {

    // --- Position Conversions ---

    @Test
    fun geoPointToPositionShouldPreserveCoordinates() {
        val point = GeoPoint(41.3111, 69.2797)
        val position = point.toPosition()
        assertEquals(41.3111, position.latitude)
        assertEquals(69.2797, position.longitude)
    }

    @Test
    fun positionToGeoPointRoundTrip() {
        val original = GeoPoint(41.3111, 69.2797)
        val roundTripped = original.toPosition().toGeoPoint()
        assertEquals(original.lat, roundTripped.lat)
        assertEquals(original.lng, roundTripped.lng)
    }

    @Test
    fun pairToPositionShouldMapFirstToLatitude() {
        val pair = 41.0 to 69.0
        val position = pair.toPosition()
        assertEquals(41.0, position.latitude)
        assertEquals(69.0, position.longitude)
    }

    @Test
    fun pairToGeoPointShouldMapFirstToLat() {
        val pair = 41.0 to 69.0
        val geoPoint = pair.toGeoPoint()
        assertEquals(41.0, geoPoint.lat)
        assertEquals(69.0, geoPoint.lng)
    }

    // --- isNonZero ---

    @Test
    fun pairIsNonZeroShouldReturnTrueForNonZeroValues() {
        assertTrue((41.0 to 69.0).isNonZero())
    }

    @Test
    fun pairIsNonZeroShouldReturnFalseWhenFirstIsZero() {
        assertFalse((0.0 to 69.0).isNonZero())
    }

    @Test
    fun pairIsNonZeroShouldReturnFalseWhenSecondIsZero() {
        assertFalse((41.0 to 0.0).isNonZero())
    }

    @Test
    fun pairIsNonZeroShouldReturnFalseWhenBothZero() {
        assertFalse((0.0 to 0.0).isNonZero())
    }

    @Test
    fun geoPointIsNonZeroShouldReturnTrueForNonZero() {
        assertTrue(GeoPoint(41.0, 69.0).isNonZero())
    }

    @Test
    fun geoPointIsNonZeroShouldReturnFalseForZero() {
        assertFalse(GeoPoint.Zero.isNonZero())
    }

    // --- isValid ---

    @Test
    fun pairIsValidShouldAcceptNormalCoordinates() {
        assertTrue((41.0 to 69.0).isValid())
    }

    @Test
    fun pairIsValidShouldRejectOutOfRangeLatitude() {
        assertFalse((91.0 to 69.0).isValid())
    }

    @Test
    fun pairIsValidShouldRejectOutOfRangeLongitude() {
        assertFalse((41.0 to 181.0).isValid())
    }

    // --- toBoundingBox ---

    @Test
    fun toBoundingBoxShouldEncloseAllPoints() {
        val points = listOf(
            GeoPoint(40.0, 68.0),
            GeoPoint(42.0, 70.0),
            GeoPoint(41.0, 69.0),
        )
        val box = points.toBoundingBox()
        assertEquals(68.0, box.southwest.longitude)
        assertEquals(40.0, box.southwest.latitude)
        assertEquals(70.0, box.northeast.longitude)
        assertEquals(42.0, box.northeast.latitude)
    }

    @Test
    fun toBoundingBoxShouldHandleSinglePoint() {
        val points = listOf(GeoPoint(41.0, 69.0))
        val box = points.toBoundingBox()
        assertEquals(41.0, box.southwest.latitude)
        assertEquals(41.0, box.northeast.latitude)
        assertEquals(69.0, box.southwest.longitude)
        assertEquals(69.0, box.northeast.longitude)
    }
}
