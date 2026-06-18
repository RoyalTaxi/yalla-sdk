package uz.yalla.maps.motion

import uz.yalla.core.geo.GeoPoint
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DriverMotionModelTest {
    private fun assertClose(
        expected: Double,
        actual: Double,
        eps: Double = 1e-6,
        msg: String = ""
    ) {
        assertTrue(abs(expected - actual) <= eps, "$msg expected~=$expected got $actual")
    }

    private fun assertBearingClose(
        expected: Float,
        actual: Float,
        eps: Float = 2f,
        msg: String = ""
    ) {
        val delta = abs(((actual - expected + 540f) % 360f) - 180f)
        assertTrue(delta <= eps, "$msg expected bearing~=$expected got $actual")
    }

    @Test
    fun firstFixSnapsAndHoldsUntilNextFix() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        m.push(a, null, null, 0L)
        assertEquals(a, m.sample(0L).point)
        assertEquals(a, m.sample(5_000L).point)
    }

    @Test
    fun interpolatesMidwayBetweenTwoFixes() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val b = GeoPoint(40.001, 71.0)
        m.push(a, null, null, 0L)
        m.push(b, null, null, 10_000L)
        val mid = m.sample(15_000L).point
        assertClose(40.0005, mid.lat, msg = "lat midpoint")
        assertClose(71.0, mid.lng, msg = "lng midpoint")
        assertClose(b.lat, m.sample(20_000L).point.lat, msg = "reaches target")
    }

    @Test
    fun stationaryDoesNotSpinToNorthOnZeroServerHeading() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        m.push(a, null, 90f, 0L)
        m.push(a, null, 0f, 10_000L)
        assertBearingClose(90f, m.sample(20_000L).bearing, msg = "held bearing")
    }

    @Test
    fun derivesBearingFromMovementDirectionEast() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val east = GeoPoint(40.0, 71.0005)
        m.push(a, null, null, 0L)
        m.push(east, null, null, 10_000L)
        assertBearingClose(90f, m.sample(20_000L).bearing, msg = "east ~= 90deg")
    }

    @Test
    fun teleportSnapsInsteadOfAnimating() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val far = GeoPoint(41.0, 71.0)
        m.push(a, null, null, 0L)
        m.push(far, null, null, 1_000L)
        assertEquals(far, m.sample(1_500L).point)
    }

    @Test
    fun midAnimationPushChainsFromCurrentDisplayedPosition() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val b = GeoPoint(40.001, 71.0)
        val c = GeoPoint(40.002, 71.0)
        m.push(a, null, null, 0L)
        m.push(b, null, null, 10_000L)
        val midAB = m.sample(15_000L).point
        m.push(c, null, null, 15_000L)
        assertClose(midAB.lat, m.sample(15_000L).point.lat, msg = "new anim starts from displayed pos")
    }

    @Test
    fun routeHintOutranksServerHeadingWhenStationary() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        m.push(a, 90f, 180f, 0L)
        assertBearingClose(90f, m.sample(0L).bearing, msg = "route hint wins over server heading")
    }

    @Test
    fun derivedMovementBearingOutranksRouteHint() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val east = GeoPoint(40.0, 71.0005)
        m.push(a, 0f, null, 0L)
        m.push(east, 270f, null, 10_000L)
        assertBearingClose(90f, m.sample(20_000L).bearing, msg = "movement direction beats a stale route hint")
    }

    @Test
    fun realNorthServerHeadingIsTreatedAsAbsent() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        m.push(a, null, 0f, 0L)
        assertBearingClose(0f, m.sample(0L).bearing, msg = "no hint resolved; defaults to 0")
    }

    @Test
    fun heldDerivedBearingSurvivesSubThresholdMoveAndIgnoresRouteHint() {
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val east = GeoPoint(40.0, 71.0005)
        m.push(a, null, null, 0L)
        m.push(east, null, null, 10_000L)
        val tiny = GeoPoint(40.0, 71.0005001)
        m.push(tiny, 270f, null, 20_000L)
        assertBearingClose(
            90f,
            m.sample(40_000L).bearing,
            msg = "a held derived bearing is not overwritten by a route hint on a sub-threshold move"
        )
    }
}
