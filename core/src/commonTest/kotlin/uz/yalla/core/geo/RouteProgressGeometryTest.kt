package uz.yalla.core.geo

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RouteProgressGeometryTest {
    // L-shaped route: east along a parallel, then a 90deg left turn to head north.
    private val corner = GeoPoint(40.0, 71.001)
    private val route =
        listOf(
            GeoPoint(40.0, 71.0),
            corner,
            GeoPoint(40.001, 71.001)
        )

    private fun assertBearingClose(
        expected: Float,
        actual: Float,
        eps: Float = 3f,
        msg: String = ""
    ) {
        val delta = abs(((actual - expected + 540f) % 360f) - 180f)
        assertTrue(delta <= eps, "$msg expected bearing~=$expected got $actual")
    }

    @Test
    fun invalidWhenFewerThanTwoDistinctPoints() {
        assertFalse(RouteProgressGeometry(emptyList()).isValid)
        assertFalse(RouteProgressGeometry(listOf(GeoPoint(40.0, 71.0))).isValid)
        // Consecutive duplicates compact down to a single point.
        assertFalse(
            RouteProgressGeometry(
                listOf(GeoPoint(40.0, 71.0), GeoPoint(40.0, 71.0))
            ).isValid
        )
        assertTrue(RouteProgressGeometry(route).isValid)
    }

    @Test
    fun projectionProgressIsMonotonicAlongTheRoute() {
        val geometry = RouteProgressGeometry(route)
        val onFirstLeg = geometry.project(GeoPoint(40.0, 71.00025)).progressMeters
        val nearCorner = geometry.project(GeoPoint(40.0, 71.00099)).progressMeters
        val onSecondLeg = geometry.project(GeoPoint(40.0005, 71.001)).progressMeters
        assertTrue(onFirstLeg < nearCorner, "first leg before corner: $onFirstLeg < $nearCorner")
        assertTrue(nearCorner < onSecondLeg, "corner before second leg: $nearCorner < $onSecondLeg")
    }

    @Test
    fun coordinateAtLandsOnThePolylineThroughTheCorner() {
        val geometry = RouteProgressGeometry(route)
        val cornerProgress = geometry.project(corner).progressMeters
        val atCorner = geometry.coordinateAt(cornerProgress)
        assertTrue(atCorner.distanceTo(corner) < 0.5, "coordinateAt corner lands on the vertex: $atCorner")

        // A point partway along the second (northbound) leg must stay on the 71.001 meridian,
        // NOT on the straight chord from the route start to the route end.
        val total = geometry.totalDistanceMeters
        val midSecondLeg = geometry.coordinateAt(total * 0.75)
        assertTrue(
            abs(midSecondLeg.lng - 71.001) < 1e-5,
            "second-leg point stays on the meridian, not the chord: $midSecondLeg"
        )
        assertTrue(midSecondLeg.lat > 40.0, "second-leg point has advanced north: $midSecondLeg")
    }

    @Test
    fun headingGivesSegmentTangentNotChord() {
        val geometry = RouteProgressGeometry(route)
        val total = geometry.totalDistanceMeters
        // Early on the first leg the tangent points east (~90deg).
        assertBearingClose(90f, geometry.headingAt(total * 0.1, fallback = 0f), msg = "first leg tangent")
        // On the second leg the tangent points north (~0/360deg), not the diagonal chord (~45deg).
        assertBearingClose(0f, geometry.headingAt(total * 0.9, fallback = 0f), msg = "second leg tangent")
    }

    @Test
    fun headingFallsBackWhenDegenerate() {
        val geometry = RouteProgressGeometry(listOf(GeoPoint(40.0, 71.0)))
        assertEquals(123f, geometry.headingAt(0.0, fallback = 123f))
    }

    @Test
    fun remainingRouteShrinksAsProgressGrows() {
        val geometry = RouteProgressGeometry(route)
        val total = geometry.totalDistanceMeters

        fun remainingMeters(progress: Double): Double {
            val pts = geometry.remainingRouteFrom(progress)
            var sum = 0.0
            for (i in 1 until pts.size) sum += pts[i - 1].distanceTo(pts[i])
            return sum
        }

        val atStart = remainingMeters(0.0)
        val atQuarter = remainingMeters(total * 0.25)
        val atThreeQuarter = remainingMeters(total * 0.75)
        assertTrue(atStart > atQuarter, "remaining shrinks: $atStart > $atQuarter")
        assertTrue(atQuarter > atThreeQuarter, "remaining keeps shrinking: $atQuarter > $atThreeQuarter")
        assertTrue(geometry.remainingRouteFrom(total).size <= 1, "arrived -> nothing left to draw")
    }

    @Test
    fun remainingRouteHeadSitsOnTheCar() {
        val geometry = RouteProgressGeometry(route)
        val progress = geometry.totalDistanceMeters * 0.6
        val head = geometry.remainingRouteFrom(progress).first()
        assertTrue(
            head.distanceTo(geometry.coordinateAt(progress)) < 0.5,
            "remaining route starts at the car position: $head"
        )
    }

    @Test
    fun offRoutePointHasLargeCrossTrack() {
        val geometry = RouteProgressGeometry(route)
        // Well east of the northbound (71.001 meridian) leg and the east leg's end.
        val off = geometry.project(GeoPoint(40.0005, 71.003))
        assertTrue(off.crossTrackMeters > 100.0, "off-route cross-track is large: ${off.crossTrackMeters}")
        // A point sitting on the first leg is essentially zero cross-track.
        val on = geometry.project(GeoPoint(40.0, 71.0005))
        assertTrue(on.crossTrackMeters < 1.0, "on-route cross-track is ~0: ${on.crossTrackMeters}")
    }

    // ----------------------------------------------------------------------------------------
    // Adversarial suite (panel edit #2). These target the forward-windowed projection that the
    // global nearest-segment scan cannot satisfy: loops, U-turns, hairpins, and near-parallel
    // grid streets where the globally-closest segment is NOT the one the car is actually on.
    // ----------------------------------------------------------------------------------------

    private fun metersEast(meters: Double): Double = meters / (METERS_PER_DEG_LAT_TEST * COS40)

    private fun metersNorth(meters: Double): Double = meters / METERS_PER_DEG_LAT_TEST

    /**
     * Figure-8: the path crosses itself. At the crossing the two legs are spatially identical, so
     * a memoryless global scan can snap the second pass back onto the first pass's arc-length and
     * "un-eat" the route. The forward window must keep progress advancing through the crossing.
     */
    @Test
    fun figureEightDoesNotUnEatAtTheSelfCrossing() {
        // A self-crossing polyline: go NE, then SE, then NW, then SW back through the centre.
        val c = GeoPoint(40.0, 71.0)
        val d = metersNorth(100.0)
        val e = metersEast(100.0)
        val fig =
            listOf(
                GeoPoint(c.lat - d, c.lng - e), // bottom-left
                GeoPoint(c.lat + d, c.lng + e), // top-right (through centre)
                GeoPoint(c.lat + d, c.lng - e), // top-left
                GeoPoint(c.lat - d, c.lng + e) // bottom-right (through centre again)
            )
        val geometry = RouteProgressGeometry(fig)

        // Walk the car along the polyline by arc length and feed each coordinate back as a GPS
        // fix. Progress recovered via the forward window must be non-decreasing.
        var last = 0.0
        var step = 0.0
        val total = geometry.totalDistanceMeters
        while (step <= total) {
            val onPath = geometry.coordinateAt(step)
            val recovered = geometry.projectForward(onPath, lastProgressMeters = last).progressMeters
            assertTrue(
                recovered >= last - 1.0,
                "figure-8 progress must not regress at the crossing: step=$step last=$last got=$recovered"
            )
            last = recovered
            step += 10.0
        }
        assertTrue(last > total * 0.9, "car reached the far end of the figure-8: $last of $total")
    }

    /**
     * U-turn: the outbound and return legs run parallel <10m apart. A point near the U-turn tip
     * is globally near BOTH legs; a memoryless scan can snap the outbound car onto the return leg
     * (a huge forward jump) or snap the returning car back onto the outbound leg (un-eat). The
     * window keeps each pass on its own leg.
     */
    @Test
    fun uTurnDoesNotSnapAcrossToTheParallelReturnLeg() {
        val sep = metersNorth(8.0) // 8m between the two parallel legs
        val uTurn =
            listOf(
                GeoPoint(40.0, 71.0),
                GeoPoint(40.0, 71.002), // out east
                GeoPoint(40.0 + sep, 71.002), // tip
                GeoPoint(40.0 + sep, 71.0) // back west, 8m north of the outbound leg
            )
        val geometry = RouteProgressGeometry(uTurn)
        val outboundLen = GeoPoint(40.0, 71.0).distanceTo(GeoPoint(40.0, 71.002))

        // A fix on the OUTBOUND leg while we believe we are early. Window must keep us outbound,
        // not snap to the return leg directly above (which is globally just as close).
        val onOutbound = GeoPoint(40.0, 71.001)
        val early = geometry.projectForward(onOutbound, lastProgressMeters = 5.0).progressMeters
        assertTrue(
            early < outboundLen + 5.0,
            "outbound fix stays on the outbound leg, not the return leg: $early vs outboundLen=$outboundLen"
        )

        // Later, a fix on the RETURN leg while we believe we are already past the tip. Window must
        // keep us on the return leg, not snap back onto the outbound leg below (un-eat).
        val onReturn = GeoPoint(40.0 + sep, 71.001)
        val pastTip = outboundLen + GeoPoint(40.0, 71.002).distanceTo(GeoPoint(40.0 + sep, 71.002))
        val late = geometry.projectForward(onReturn, lastProgressMeters = pastTip + 5.0).progressMeters
        assertTrue(late > pastTip, "return fix stays on the return leg, not the outbound leg: $late vs $pastTip")
    }

    /**
     * Hairpin: a sharp >150deg switchback. Like the U-turn but the legs touch at the tip, so the
     * globally nearest segment flips abruptly. The window must not teleport progress across the tip
     * before the car has actually driven there.
     */
    @Test
    fun hairpinKeepsProgressOnTheApproachLegBeforeTheTip() {
        val hairpin =
            listOf(
                GeoPoint(40.0, 71.0),
                GeoPoint(40.0, 71.001), // approach east
                GeoPoint(40.0 + metersNorth(5.0), 71.0) // fold back, 5m north
            )
        val geometry = RouteProgressGeometry(hairpin)
        val approachLen = GeoPoint(40.0, 71.0).distanceTo(GeoPoint(40.0, 71.001))

        // A fix on the approach leg, believing we are early. A global scan could snap to the
        // return leg (it folds back over the same longitudes). The window keeps us on approach.
        val onApproach = GeoPoint(40.0, 71.0005)
        val p = geometry.projectForward(onApproach, lastProgressMeters = 2.0).progressMeters
        assertTrue(p < approachLen + 5.0, "hairpin approach fix stays before the tip: $p vs $approachLen")
    }

    /**
     * Two near-parallel grid streets <30m apart with a GPS bounce. The car is on street A; a noisy
     * fix lands closer to street B. A global scan jumps progress onto B; the bounded window rejects
     * B (outside the window) and falls back to the nearest in-window point on A.
     */
    @Test
    fun parallelStreetGpsBounceStaysOnTheCurrentStreet() {
        // Two straight E-W segments stacked into one polyline via a connector, 20m apart.
        val sep = metersNorth(20.0)
        // Single straight street A (east) for clarity: the "other street" is represented as a fix
        // offset 20m north, well within global reach but outside a tight cross-window.
        val streetA =
            listOf(
                GeoPoint(40.0, 71.0),
                GeoPoint(40.0, 71.003)
            )
        val geometry = RouteProgressGeometry(streetA)
        val carProgress = geometry.coordinateAt(geometry.totalDistanceMeters * 0.5)
        val onA = geometry.projectForward(carProgress, lastProgressMeters = geometry.totalDistanceMeters * 0.5)
        // A bounce 20m north (toward "street B") keeps roughly the same arc-length: it does not
        // leap forward/backward, because the window anchors progress near where the car was.
        val bounced = GeoPoint(40.0 + sep, carProgress.lng)
        val afterBounce =
            geometry.projectForward(bounced, lastProgressMeters = onA.progressMeters).progressMeters
        assertTrue(
            abs(afterBounce - onA.progressMeters) < 25.0,
            "GPS bounce toward a parallel street does not leap progress: ${onA.progressMeters} -> $afterBounce"
        )
    }

    /**
     * Monotonic-never-teleports: a forward GPS fix advances progress by roughly the real driven
     * distance, never by a wild global jump, and never backward beyond the small back window.
     */
    @Test
    fun forwardWindowAdvancesSmoothlyAndNeverTeleports() {
        val geometry = RouteProgressGeometry(route)
        val total = geometry.totalDistanceMeters
        var last = 0.0
        var step = 0.0
        while (step <= total) {
            val onPath = geometry.coordinateAt(step)
            val recovered = geometry.projectForward(onPath, lastProgressMeters = last).progressMeters
            // Forward, by roughly one step, never a teleport beyond the forward window.
            assertTrue(recovered >= last - 1.0, "never regresses beyond back window: $last -> $recovered")
            assertTrue(recovered <= last + 60.0, "never teleports past the forward window: $last -> $recovered")
            last = recovered
            step += 10.0
        }
    }

    /**
     * A legitimate backward sample (real reverse) within the back window must NOT freeze: progress
     * is allowed to decrease inside `[lastProgress - back, ...]` rather than being hard-clamped.
     */
    @Test
    fun legitimateReverseWithinBackWindowDecreasesProgress() {
        val geometry = RouteProgressGeometry(route)
        val start = 30.0
        val behind = geometry.coordinateAt(start - 3.0)
        val recovered =
            geometry
                .projectForward(
                    behind,
                    lastProgressMeters = start,
                    backWindowMeters = 5.0
                ).progressMeters
        assertTrue(recovered < start, "a real reverse within the back window decreases progress: $start -> $recovered")
        assertTrue(recovered >= start - 6.0, "but not beyond the back window: $recovered")
    }

    /**
     * Arrival / sharp-vertex heading: heading at the very end of the route stays the final segment
     * tangent and does not flip wildly as the lead points clamp against the route end.
     */
    @Test
    fun headingAtArrivalStaysTheFinalSegmentTangent() {
        val geometry = RouteProgressGeometry(route)
        val total = geometry.totalDistanceMeters
        // The final leg heads north (~0deg). At and just before arrival the heading must hold north.
        assertBearingClose(0f, geometry.headingAt(total, fallback = 123f), eps = 5f, msg = "arrival heading")
        assertBearingClose(0f, geometry.headingAt(total - 0.5, fallback = 123f), eps = 5f, msg = "just before arrival")
    }

    private companion object {
        const val METERS_PER_DEG_LAT_TEST = 111_320.0
        const val COS40 = 0.766_044_443_118_978 // cos(40deg)
    }
}
