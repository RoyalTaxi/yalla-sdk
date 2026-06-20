package uz.yalla.core.geo

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Output-based characterization of [distanceTo] (haversine on a 6 371 000 m sphere).
 *
 * Great-circle distance is asserted against independently computed reference values within a
 * tolerance, not recomputed with the same formula — so a swapped lat/lng, a missing
 * degrees-to-radians conversion, or a wrong Earth radius all fail here. Reference values come
 * from the same spherical model (R = 6 371 000 m) the implementation uses.
 */
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
        // A meridian degree on a 6 371 000 m sphere is ~111 195 m.
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(1.0, 0.0)
        assertClose(111_194.93, a.distanceTo(b), tolerance = 1.0)
    }

    @Test
    fun oneDegreeOfLongitudeAtTheEquatorEqualsOneDegreeOfLatitude() {
        // On the equator a longitude degree spans the same arc length as a latitude degree.
        val a = GeoPoint(0.0, 0.0)
        val b = GeoPoint(0.0, 1.0)
        assertClose(111_194.93, a.distanceTo(b), tolerance = 1.0)
    }

    @Test
    fun knownCityPairMatchesReferenceGreatCircle() {
        // Reference: 267 014 m (haversine, R = 6 371 000 m).
        assertClose(267_014.16, tashkent.distanceTo(samarkand), tolerance = 5.0)
    }

    @Test
    fun longHaulPairMatchesReferenceGreatCircle() {
        // London -> Sydney, reference 16 993 933 m.
        val london = GeoPoint(51.5074, -0.1278)
        val sydney = GeoPoint(-33.8688, 151.2093)
        assertClose(16_993_933.46, london.distanceTo(sydney), tolerance = 50.0)
    }

    @Test
    fun crossesEquatorAndPrimeMeridianCorrectly() {
        // New York -> Los Angeles, reference 3 935 746 m.
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

    // --- bearingTo: compass degrees clockwise from north, normalized to [0, 360) ---

    private fun assertBearingClose(
        expected: Double,
        actual: Double,
        eps: Double = 1.0
    ) {
        // Compare on the circle so 359.9 vs 0.1 is close, not ~360 apart.
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
        // Exercises the negative-atan2 -> +360 wrap branch the route test never reaches.
        assertBearingClose(180.0, GeoPoint(1.0, 0.0).bearingTo(GeoPoint(0.0, 0.0)))
    }

    @Test
    fun bearingPointsWestForADueWestTarget() {
        // The other half of the south/west normalization branch.
        assertBearingClose(270.0, GeoPoint(0.0, 1.0).bearingTo(GeoPoint(0.0, 0.0)))
    }

    @Test
    fun bearingIsRoughly45ForANortheastTargetAtTheEquator() {
        // At the equator a small NE step is ~45 deg; independently expected, not recomputed.
        assertBearingClose(45.0, GeoPoint(0.0, 0.0).bearingTo(GeoPoint(0.001, 0.001)), eps = 0.5)
    }

    @Test
    fun bearingIsRoughly225ForASouthwestTargetAtTheEquator() {
        assertBearingClose(225.0, GeoPoint(0.0, 0.0).bearingTo(GeoPoint(-0.001, -0.001)), eps = 0.5)
    }

    @Test
    fun bearingIsEastWhenCrossingTheAntimeridian() {
        // From lng 179 to lng -179 is a 2-degree step EAST across the antimeridian, not west.
        assertBearingClose(90.0, GeoPoint(0.0, 179.0).bearingTo(GeoPoint(0.0, -179.0)))
    }

    @Test
    fun bearingForIdenticalPointsIsZero() {
        assertEquals(0.0, tashkent.bearingTo(tashkent))
    }
}
