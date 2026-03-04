package uz.yalla.core.geo

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GeoPointTest {
    @Test
    fun shouldCreatePointWhenCoordinatesAreWithinBounds() {
        val point = GeoPoint(lat = 41.3111, lng = 69.2797)

        assertEquals(41.3111, point.lat)
        assertEquals(69.2797, point.lng)
    }

    @Test
    fun shouldExposeZeroPointConstant() {
        assertEquals(GeoPoint(0.0, 0.0), GeoPoint.Zero)
    }

    @Test
    fun shouldThrowWhenLatitudeIsBelowMinimum() {
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(lat = -90.00001, lng = 0.0)
        }
    }

    @Test
    fun shouldThrowWhenLatitudeIsAboveMaximum() {
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(lat = 90.00001, lng = 0.0)
        }
    }

    @Test
    fun shouldThrowWhenLongitudeIsBelowMinimum() {
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(lat = 0.0, lng = -180.00001)
        }
    }

    @Test
    fun shouldThrowWhenLongitudeIsAboveMaximum() {
        assertFailsWith<IllegalArgumentException> {
            GeoPoint(lat = 0.0, lng = 180.00001)
        }
    }

    @Test
    fun shouldReturnZeroDistanceWhenComparingPointToItself() {
        val point = GeoPoint(lat = 41.3111, lng = 69.2797)

        val distance = point.distanceTo(point)

        assertTrue(distance >= 0.0)
        assertTrue(abs(distance) <= 1e-9)
    }

    @Test
    fun shouldReturnSymmetricDistanceBetweenTwoPoints() {
        val pointA = GeoPoint(lat = 41.3111, lng = 69.2797)
        val pointB = GeoPoint(lat = 39.6542, lng = 66.9597)

        val forwardDistance = pointA.distanceTo(pointB)
        val backwardDistance = pointB.distanceTo(pointA)

        assertTrue(abs(forwardDistance - backwardDistance) <= 1e-6)
    }

    @Test
    fun shouldReturnReasonableDistanceForKnownCities() {
        val tashkent = GeoPoint(lat = 41.3111, lng = 69.2797)
        val samarkand = GeoPoint(lat = 39.6542, lng = 66.9597)

        val distanceMeters = tashkent.distanceTo(samarkand)

        assertTrue(distanceMeters in 250_000.0..300_000.0)
    }
}
