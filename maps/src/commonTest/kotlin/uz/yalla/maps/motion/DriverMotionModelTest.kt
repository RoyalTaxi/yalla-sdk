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
    fun sparseGpsMoveAnimatesInsteadOfFalselySnapping() {
        // 700 m north over 30 s = 23 m/s, well under the 50 m/s teleport threshold. Before the fix
        // the elapsed time was clamped to 12 s, inflating the implied speed to ~58 m/s and falsely
        // snapping. With the raw elapsed time used for the speed test, this must animate.
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val b = GeoPoint(40.006_288, 71.0) // ~700 m north of a
        m.push(a, null, null, 0L)
        m.push(b, null, null, 30_000L)
        // Mid-way through the animation the marker is between a and b, not snapped to b.
        val mid = m.sample(36_000L).point
        assertTrue(mid.lat > a.lat, "should have moved off the start")
        assertTrue(mid.lat < b.lat, "should not have snapped to the target: lat=${mid.lat}")
    }

    @Test
    fun subSecondJumpSnapsInsteadOfAnimating() {
        // 30 m in 200 ms = 150 m/s, far above the 50 m/s threshold. Before the fix the elapsed
        // time was clamped up to 1 s, deflating the implied speed to 30 m/s and animating a real
        // teleport. With the raw 200 ms used, this must snap to the target immediately.
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val jump = GeoPoint(40.000_269_5, 71.0) // ~30 m north of a
        m.push(a, null, null, 0L)
        m.push(jump, null, null, 200L)
        assertEquals(jump, m.sample(300L).point)
    }

    @Test
    fun resumesAnimatingAfterATeleportSnap() {
        // After a snap, a normal-speed fix must resume animating from the snapped position.
        val m = DriverMotionModel()
        val a = GeoPoint(40.0, 71.0)
        val far = GeoPoint(41.0, 71.0)
        m.push(a, null, null, 0L)
        m.push(far, null, null, 200L) // huge jump in 200ms -> snap
        assertEquals(far, m.sample(300L).point)
        val next = GeoPoint(41.001, 71.0) // small normal move
        m.push(next, null, null, 10_200L)
        val mid = m.sample(15_200L).point
        assertTrue(mid.lat > far.lat, "should have left the snapped position")
        assertTrue(mid.lat < next.lat, "should be animating, not snapped: lat=${mid.lat}")
    }

    @Test
    fun hasNoFixBeforeFirstPushThenReportsFix() {
        val m = DriverMotionModel()
        assertTrue(!m.hasFix(), "no fix before first push")
        assertEquals(GeoPoint.Zero, m.sample(0L).point)
        m.push(GeoPoint(40.0, 71.0), null, null, 0L)
        assertTrue(m.hasFix(), "has fix after first push")
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
