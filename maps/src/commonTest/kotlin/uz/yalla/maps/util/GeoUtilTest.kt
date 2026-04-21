package uz.yalla.maps.util

import uz.yalla.core.geo.GeoPoint
import uz.yalla.maps.model.LatLng
import uz.yalla.maps.model.LatLngBounds
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoUtilTest {

    // ============================================
    // haversineDistance
    // ============================================

    @Test
    fun haversineDistanceSamePointReturnsZero() {
        val point = GeoPoint(lat = 41.3111, lng = 69.2797)
        val distance = haversineDistance(point, point)
        assertTrue(abs(distance) < 1e-9, "Distance from a point to itself should be 0, was $distance")
    }

    @Test
    fun haversineDistanceIsSymmetric() {
        val tashkent = GeoPoint(lat = 41.3111, lng = 69.2797)
        val samarkand = GeoPoint(lat = 39.6542, lng = 66.9597)

        val forward = haversineDistance(tashkent, samarkand)
        val backward = haversineDistance(samarkand, tashkent)

        assertTrue(abs(forward - backward) < 1e-6, "Haversine should be symmetric: $forward vs $backward")
    }

    @Test
    fun haversineDistanceTashkentToSamarkandIsReasonable() {
        val tashkent = GeoPoint(lat = 41.3111, lng = 69.2797)
        val samarkand = GeoPoint(lat = 39.6542, lng = 66.9597)

        val distanceKm = haversineDistance(tashkent, samarkand)

        // Known distance Tashkent-Samarkand is ~270 km
        assertTrue(distanceKm in 250.0..300.0, "Tashkent-Samarkand should be ~270km, was $distanceKm km")
    }

    @Test
    fun haversineDistanceLondonToNewYorkIsReasonable() {
        val london = GeoPoint(lat = 51.5074, lng = -0.1278)
        val newYork = GeoPoint(lat = 40.7128, lng = -74.0060)

        val distanceKm = haversineDistance(london, newYork)

        // Known distance London-NYC is ~5570 km
        assertTrue(distanceKm in 5500.0..5650.0, "London-NYC should be ~5570km, was $distanceKm km")
    }

    @Test
    fun haversineDistanceAntipodalPointsDoNotProduceNaN() {
        // North pole to south pole -- tests the coerceIn fix for near-antipodal points
        val northPole = GeoPoint(lat = 90.0, lng = 0.0)
        val southPole = GeoPoint(lat = -90.0, lng = 0.0)

        val distanceKm = haversineDistance(northPole, southPole)

        // Half earth circumference is ~20015 km
        assertTrue(distanceKm in 20000.0..20050.0, "Pole-to-pole should be ~20015km, was $distanceKm km")
        assertFalse(distanceKm.isNaN(), "Distance should not be NaN for antipodal points")
    }

    @Test
    fun haversineDistanceRawCoordsMatchesGeoPointOverload() {
        val lat1 = 41.3111
        val lng1 = 69.2797
        val lat2 = 39.6542
        val lng2 = 66.9597

        val rawDistance = haversineDistance(lat1, lng1, lat2, lng2)
        val geoDistance = haversineDistance(GeoPoint(lat1, lng1), GeoPoint(lat2, lng2))

        assertTrue(abs(rawDistance - geoDistance) < 1e-9, "Both overloads should give same result")
    }

    // ============================================
    // normalizeHeading
    // ============================================

    @Test
    fun normalizeHeadingZeroReturnsZero() {
        assertEquals(0f, normalizeHeading(0f))
    }

    @Test
    fun normalizeHeading360ReturnsZero() {
        assertEquals(0f, normalizeHeading(360f))
    }

    @Test
    fun normalizeHeading90Returns90() {
        assertEquals(90f, normalizeHeading(90f))
    }

    @Test
    fun normalizeHeading359Returns359() {
        // Values within [0, 360) should remain unchanged
        val result = normalizeHeading(359f)
        assertTrue(abs(result - 359f) < 0.001f, "359 should remain 359, was $result")
    }

    @Test
    fun normalizeHeadingNegative90Returns270() {
        assertEquals(270f, normalizeHeading(-90f))
    }

    @Test
    fun normalizeHeadingNegative180Returns180() {
        assertEquals(180f, normalizeHeading(-180f))
    }

    @Test
    fun normalizeHeadingNegative360ReturnsZero() {
        assertEquals(0f, normalizeHeading(-360f))
    }

    @Test
    fun normalizeHeading720ReturnsZero() {
        assertEquals(0f, normalizeHeading(720f))
    }

    @Test
    fun normalizeHeading450Returns90() {
        assertEquals(90f, normalizeHeading(450f))
    }

    @Test
    fun normalizeHeadingNegative450Returns270() {
        assertEquals(270f, normalizeHeading(-450f))
    }

    // ============================================
    // shortestHeadingPath
    // ============================================

    @Test
    fun shortestHeadingPathSameHeadingReturnsCurrent() {
        val result = shortestHeadingPath(current = 90f, target = 90f)
        assertTrue(abs(result - 90f) < 0.001f, "Same heading should return current, was $result")
    }

    @Test
    fun shortestHeadingPathSmallClockwiseDelta() {
        // From 10 to 20 should go +10 (clockwise)
        val result = shortestHeadingPath(current = 10f, target = 20f)
        assertTrue(abs(result - 20f) < 0.001f, "10->20 should return 20, was $result")
    }

    @Test
    fun shortestHeadingPathSmallCounterclockwiseDelta() {
        // From 20 to 10 should go -10 (counter-clockwise)
        val result = shortestHeadingPath(current = 20f, target = 10f)
        assertTrue(abs(result - 10f) < 0.001f, "20->10 should return 10, was $result")
    }

    @Test
    fun shortestHeadingPathCrossingZeroClockwise() {
        // From 350 to 10: clockwise is 20 degrees, counter-clockwise is 340 degrees
        // Should choose clockwise: current + 20 = 370
        val result = shortestHeadingPath(current = 350f, target = 10f)
        assertTrue(abs(result - 370f) < 0.001f, "350->10 should be 370 (CW wrap), was $result")
    }

    @Test
    fun shortestHeadingPathCrossingZeroCounterclockwise() {
        // From 10 to 350: counter-clockwise is 20 degrees, clockwise is 340 degrees
        // Should choose counter-clockwise: current - 20 = -10
        val result = shortestHeadingPath(current = 10f, target = 350f)
        assertTrue(abs(result - (-10f)) < 0.001f, "10->350 should be -10 (CCW wrap), was $result")
    }

    @Test
    fun shortestHeadingPath180DegreeDelta() {
        // From 0 to 180: exactly 180 degrees, should go positive (delta > 180 check is strict >)
        val result = shortestHeadingPath(current = 0f, target = 180f)
        assertTrue(abs(result - 180f) < 0.001f, "0->180 should be 180, was $result")
    }

    // ============================================
    // LatLng.isValid()
    // ============================================

    @Test
    fun latLngIsValidForNormalCoordinates() {
        assertTrue(LatLng(41.3111, 69.2797).isValid())
    }

    @Test
    fun latLngIsValidForZeroZero() {
        assertTrue(LatLng(0.0, 0.0).isValid())
    }

    @Test
    fun latLngIsValidForBoundaryValues() {
        assertTrue(LatLng(90.0, 180.0).isValid())
        assertTrue(LatLng(-90.0, -180.0).isValid())
    }

    @Test
    fun latLngIsInvalidWhenLatitudeOutOfRange() {
        assertFalse(LatLng(91.0, 0.0).isValid())
        assertFalse(LatLng(-91.0, 0.0).isValid())
    }

    @Test
    fun latLngIsInvalidWhenLongitudeOutOfRange() {
        assertFalse(LatLng(0.0, 181.0).isValid())
        assertFalse(LatLng(0.0, -181.0).isValid())
    }

    // ============================================
    // GeoPoint.isNonZero() / Pair.isNonZero()
    // ============================================

    @Test
    fun geoPointIsNonZeroReturnsTrueForNonZeroCoordinates() {
        assertTrue(GeoPoint(41.3111, 69.2797).isNonZero())
    }

    @Test
    fun geoPointIsNonZeroReturnsFalseForZeroZero() {
        assertFalse(GeoPoint.Zero.isNonZero())
    }

    @Test
    fun geoPointIsNonZeroReturnsFalseWhenOnlyLatIsZero() {
        assertFalse(GeoPoint(0.0, 69.0).isNonZero())
    }

    @Test
    fun geoPointIsNonZeroReturnsFalseWhenOnlyLngIsZero() {
        assertFalse(GeoPoint(41.0, 0.0).isNonZero())
    }

    @Test
    fun pairIsNonZeroReturnsTrueForNonZeroPair() {
        assertTrue((41.0 to 69.0).isNonZero())
    }

    @Test
    fun pairIsNonZeroReturnsFalseForZeroPair() {
        assertFalse((0.0 to 0.0).isNonZero())
    }

    // ============================================
    // Pair.isValid()
    // ============================================

    @Test
    fun pairIsValidForNormalCoordinates() {
        assertTrue((41.0 to 69.0).isValid())
    }

    @Test
    fun pairIsValidForZeroZero() {
        assertTrue((0.0 to 0.0).isValid())
    }

    @Test
    fun pairIsInvalidWhenLatOutOfRange() {
        assertFalse((91.0 to 0.0).isValid())
    }

    @Test
    fun pairIsInvalidWhenLngOutOfRange() {
        assertFalse((0.0 to 181.0).isValid())
    }

    // ============================================
    // LatLngBounds.Builder
    // ============================================

    @Test
    fun boundsBuilderThrowsWhenNoPointsIncluded() {
        assertFailsWith<IllegalArgumentException> {
            LatLngBounds.Builder().build()
        }
    }

    @Test
    fun boundsBuilderSinglePointMakesDegenerateBounds() {
        val point = LatLng(41.3111, 69.2797)
        val bounds = LatLngBounds.Builder().include(point).build()

        assertEquals(point.latitude, bounds.southwest.latitude)
        assertEquals(point.longitude, bounds.southwest.longitude)
        assertEquals(point.latitude, bounds.northeast.latitude)
        assertEquals(point.longitude, bounds.northeast.longitude)
    }

    @Test
    fun boundsBuilderMultiplePointsSpanCorrectly() {
        val builder = LatLngBounds
            .Builder()
            .include(LatLng(10.0, 20.0))
            .include(LatLng(30.0, 40.0))
            .include(LatLng(20.0, 30.0))

        val bounds = builder.build()

        assertEquals(10.0, bounds.southwest.latitude)
        assertEquals(20.0, bounds.southwest.longitude)
        assertEquals(30.0, bounds.northeast.latitude)
        assertEquals(40.0, bounds.northeast.longitude)
    }

    @Test
    fun boundsBuilderChainingWorks() {
        val bounds = LatLngBounds
            .Builder()
            .include(LatLng(0.0, 0.0))
            .include(LatLng(10.0, 10.0))
            .build()

        assertEquals(0.0, bounds.southwest.latitude)
        assertEquals(10.0, bounds.northeast.latitude)
    }

    @Test
    fun boundsCenterIsCorrectForSymmetricBounds() {
        val bounds = LatLngBounds(
            southwest = LatLng(10.0, 20.0),
            northeast = LatLng(30.0, 40.0)
        )

        val center = bounds.center

        assertEquals(20.0, center.latitude)
        assertEquals(30.0, center.longitude)
    }

    @Test
    fun boundsCenterForDegenerateBoundsIsThePoint() {
        val point = LatLng(41.3111, 69.2797)
        val bounds = LatLngBounds(southwest = point, northeast = point)

        val center = bounds.center

        assertEquals(point.latitude, center.latitude)
        assertEquals(point.longitude, center.longitude)
    }

    @Test
    fun boundsBuilderWithNegativeCoordinates() {
        val bounds = LatLngBounds
            .Builder()
            .include(LatLng(-10.0, -20.0))
            .include(LatLng(10.0, 20.0))
            .build()

        assertEquals(-10.0, bounds.southwest.latitude)
        assertEquals(-20.0, bounds.southwest.longitude)
        assertEquals(10.0, bounds.northeast.latitude)
        assertEquals(20.0, bounds.northeast.longitude)
    }
}
