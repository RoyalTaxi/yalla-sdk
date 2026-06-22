package uz.yalla.core.geo

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GeoDistanceTest {
    private val tashkent = GeoPoint(41.2995, 69.2401)
    private val samarkand = GeoPoint(39.6270, 66.9750)

    private fun assertClose(
        expected: Double,
        actual: Double,
        tolerance: Double
    ) {
        assertTrue(
            abs(expected - actual) <= tolerance,
            "expected $expected within $tolerance of actual $actual (delta ${abs(expected - actual)})"
        )
    }

    @Test
    fun distanceToSamePointIsExactlyZero() {
        assertEquals(0.0, tashkent.distanceTo(tashkent))
        assertEquals(0.0, GeoPoint.Zero.distanceTo(GeoPoint.Zero))
    }

    @Test
    fun oneDegreeOfLatitudeIsRoughlyOneEleventhOfADegreeArc() {
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(1.0, 0.0)
        assertClose(111_194.93, a.distanceTo(b), tolerance = 1.0)
    }

    @Test
    fun oneDegreeOfLongitudeAtTheEquatorEqualsOneDegreeOfLatitude() {
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(0.0, 1.0)
        assertClose(111_194.93, a.distanceTo(b), tolerance = 1.0)
    }

    @Test
    fun knownCityPairMatchesReferenceGreatCircle() {
        assertClose(267_014.16, tashkent.distanceTo(samarkand), tolerance = 5.0)
    }

    @Test
    fun longHaulPairMatchesReferenceGreatCircle() {
        val london = GeoPoint(51.5074, -0.1278)
        val sydney = GeoPoint(-33.8688, 151.2093)
        assertClose(16_993_933.46, london.distanceTo(sydney), tolerance = 50.0)
    }

    @Test
    fun crossesEquatorAndPrimeMeridianCorrectly() {
        val newYork = GeoPoint(40.7128, -74.0060)
        val losAngeles = GeoPoint(34.0522, -118.2437)
        assertClose(3_935_746.25, newYork.distanceTo(losAngeles), tolerance = 20.0)
    }

    @Test
    fun isSymmetric() {
        assertEquals(
            tashkent.distanceTo(samarkand),
            samarkand.distanceTo(tashkent),
            absoluteTolerance = 1e-6
        )
    }

    @Test
    fun isPositiveForDistinctPoints() {
        assertTrue(tashkent.distanceTo(samarkand) > 0.0)
    }

    private fun assertBearingClose(
        expected: Double,
        actual: Double,
        eps: Double = 1.0
    ) {
        val delta = abs(((actual - expected + 540.0) % 360.0) - 180.0)
        assertTrue(delta <= eps, "expected bearing~=$expected got $actual")
    }

    @Test
    fun bearingPointsNorthForADueNorthTarget() {
        assertBearingClose(0.0, GeoPoint(0.0, 0.0).bearingTo(GeoPoint(1.0, 0.0)))
    }

    @Test
    fun bearingPointsEastForADueEastTarget() {
        assertBearingClose(90.0, GeoPoint(0.0, 0.0).bearingTo(GeoPoint(0.0, 1.0)))
    }

    @Test
    fun bearingPointsSouthForADueSouthTarget() {
        assertBearingClose(180.0, GeoPoint(1.0, 0.0).bearingTo(GeoPoint(0.0, 0.0)))
    }

    @Test
    fun bearingPointsWestForADueWestTarget() {
        assertBearingClose(270.0, GeoPoint(0.0, 1.0).bearingTo(GeoPoint(0.0, 0.0)))
    }

    @Test
    fun bearingIsRoughly45ForANortheastTargetAtTheEquator() {
        assertBearingClose(45.0, GeoPoint(0.0, 0.0).bearingTo(GeoPoint(0.001, 0.001)), eps = 0.5)
    }

    @Test
    fun bearingIsRoughly225ForASouthwestTargetAtTheEquator() {
        assertBearingClose(225.0, GeoPoint(0.0, 0.0).bearingTo(GeoPoint(-0.001, -0.001)), eps = 0.5)
    }

    @Test
    fun bearingIsEastWhenCrossingTheAntimeridian() {
        assertBearingClose(90.0, GeoPoint(0.0, 179.0).bearingTo(GeoPoint(0.0, -179.0)))
    }

    @Test
    fun bearingForIdenticalPointsIsZero() {
        assertEquals(0.0, tashkent.bearingTo(tashkent))
    }
}
