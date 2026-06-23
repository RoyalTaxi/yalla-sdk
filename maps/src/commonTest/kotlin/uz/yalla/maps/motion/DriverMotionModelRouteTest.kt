package uz.yalla.maps.motion

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.geo.distanceTo
import uz.yalla.maps.config.RouteFollowingConfig
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DriverMotionModelRouteTest {
    // L-shaped route: east, then a 90deg turn north.
    private val corner = GeoPoint(40.0, 71.001)
    private val routeEnd = GeoPoint(40.001, 71.001)
    private val route =
        listOf(
            GeoPoint(40.0, 71.0),
            corner,
            routeEnd
        )

    private fun routeModel(config: RouteFollowingConfig = RouteFollowingConfig()): DriverMotionModel =
        DriverMotionModel(routeFollowingEnabled = true, routeConfig = config)

    private fun headingDelta(
        a: Float,
        b: Float
    ): Float = abs(((b - a + 540f) % 360f) - 180f)

    // ----- Feature flag (edit #7): default OFF -> chord, even when a route is set. -----

    @Test
    fun routeFollowingDefaultsOffSoSetRouteIsAChordNoop() {
        val m = DriverMotionModel() // default: routeFollowingEnabled = false
        m.setRoute(route)
        assertFalse(m.isFollowingRoute(), "flag default OFF: setRoute must not switch to route mode")
        // Two fixes that straddle the corner -> chord cuts the diagonal, does NOT hug the polyline.
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        m.push(GeoPoint(40.0003, 71.001), null, null, 10_000L)
        var minDistToCorner = Double.MAX_VALUE
        var t = 10_000L
        while (t <= 20_000L) {
            minDistToCorner = minOf(minDistToCorner, m.sample(t).point.distanceTo(corner))
            t += 500L
        }
        assertTrue(minDistToCorner > 20.0, "flag off: chord cuts the corner, stays far from the vertex")
    }

    @Test
    fun enablingTheFlagActivatesRouteMode() {
        val m = routeModel()
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        assertTrue(m.isFollowingRoute(), "flag on + valid route + near fix -> following route")
    }

    // ----- Eating + corner hugging (edit #4): position stays on the polyline. -----

    @Test
    fun positionStaysOnRouteAroundTheCornerNotOnTheChord() {
        val m = routeModel()
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        m.push(GeoPoint(40.0003, 71.001), null, null, 10_000L)

        var minDistToCorner = Double.MAX_VALUE
        var maxDistToChord = 0.0
        val chordStart = GeoPoint(40.0, 71.0002)
        val chordEnd = GeoPoint(40.0003, 71.001)
        val chordMid =
            GeoPoint(
                lat = (chordStart.lat + chordEnd.lat) / 2,
                lng = (chordStart.lng + chordEnd.lng) / 2
            )
        var t = 10_000L
        while (t <= 20_000L) {
            val p = m.sample(t).point
            minDistToCorner = minOf(minDistToCorner, p.distanceTo(corner))
            maxDistToChord = maxOf(maxDistToChord, p.distanceTo(chordMid))
            t += 500L
        }
        assertTrue(minDistToCorner < 15.0, "car passes near the corner vertex: $minDistToCorner m")
        assertTrue(maxDistToChord > 20.0, "car departs the straight chord: $maxDistToChord m")
    }

    @Test
    fun headingTurnsSmoothlyWithoutExceedingTheCap() {
        val capPerSecond = 120.0
        val m = routeModel(RouteFollowingConfig(maxHeadingTurnRatePerSecond = capPerSecond))
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        m.push(GeoPoint(40.0003, 71.001), null, null, 10_000L)

        var previous = m.sample(10_000L).bearing
        var previousMs = 10_000L
        var t = 10_200L
        while (t <= 20_000L) {
            val bearing = m.sample(t).bearing
            val dtSeconds = (t - previousMs) / 1000.0
            val maxAllowed = (capPerSecond * dtSeconds + 0.5).toFloat()
            assertTrue(
                headingDelta(previous, bearing) <= maxAllowed,
                "heading step ${headingDelta(previous, bearing)} <= cap $maxAllowed at $t ms"
            )
            previous = bearing
            previousMs = t
            t += 200L
        }
        assertTrue(headingDelta(m.sample(20_000L).bearing, 0f) < 30f, "ends pointing roughly north")
    }

    // ----- Monotonic forward window (edit #1): a backward near sample never regresses. -----

    @Test
    fun progressNeverRegressesOnABackwardGpsSample() {
        val m = routeModel()
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        m.push(GeoPoint(40.0, 71.0008), null, null, 10_000L)
        val advanced = m.sample(10_000L).point
        // A noisy GPS sample that lands BEHIND the car on the route (within the back window).
        m.push(GeoPoint(40.0, 71.0007), null, null, 20_000L)
        val afterBackward = m.sample(20_000L).point
        assertTrue(
            afterBackward.lng >= advanced.lng - 1e-5,
            "car must not jump backward: before=${advanced.lng} after=${afterBackward.lng}"
        )
        assertEquals(RouteState.ON_ROUTE, m.routeState(), "a backward but near sample is still on-route")
    }

    /**
     * Adversarial: a parallel return leg <30m away. Without the forward window the car would snap
     * across to the globally-nearest parallel segment and teleport. With it, the car hugs its own
     * leg. (This is the bug the draft's global scan had.)
     */
    @Test
    fun doesNotTeleportAcrossToANearParallelReturnLeg() {
        val sep = 0.00018 // ~20m north
        val uTurn =
            listOf(
                GeoPoint(40.0, 71.0),
                GeoPoint(40.0, 71.002),
                GeoPoint(40.0 + sep, 71.002),
                GeoPoint(40.0 + sep, 71.0)
            )
        val m = routeModel()
        m.setRoute(uTurn)
        // Drive along the OUTBOUND leg. Each fix is on the lower leg; the upper (return) leg is
        // only ~20m away and globally just as close near the same longitudes.
        m.push(GeoPoint(40.0, 71.0003), null, null, 0L)
        m.push(GeoPoint(40.0, 71.0009), null, null, 10_000L)
        val mid = m.sample(15_000L).point
        // The car must still be on the outbound (lat ~ 40.0) leg, not snapped up onto the return leg.
        assertTrue(mid.lat < 40.0 + sep / 2, "stays on the outbound leg, not the parallel return leg: ${mid.lat}")
    }

    // ----- Binary off-route state + connector + hysteresis + edge-latch (edit #5). -----

    @Test
    fun offRouteGpsFallsBackToChordAndSurfacesOffRouteState() {
        val m = routeModel()
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        assertEquals(RouteState.ON_ROUTE, m.routeState(), "on-route after a near fix")
        assertTrue(m.isFollowingRoute(), "following route after a near fix")

        val far = GeoPoint(40.002, 71.0005) // ~200m off
        m.push(far, null, null, 10_000L)
        assertEquals(RouteState.OFF_ROUTE, m.routeState(), "far fix flips to OFF_ROUTE")
        assertFalse(m.isFollowingRoute(), "off-route falls out of route mode")
        assertTrue(m.sample(30_000L).point.distanceTo(far) < 1.0, "chord fallback drives toward the raw fix")
    }

    @Test
    fun offRouteSignalIsEdgeLatchedNotPerFrame() {
        val m = routeModel(RouteFollowingConfig(refetchCooldownMillis = 0L))
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        assertFalse(m.consumeOffRouteSignal(), "no signal while on-route")

        val far = GeoPoint(40.002, 71.0005)
        m.push(far, null, null, 10_000L)
        assertTrue(m.consumeOffRouteSignal(), "crossing into OFF_ROUTE latches exactly one signal")
        assertFalse(m.consumeOffRouteSignal(), "signal consumed -> no repeat (no refetch storm)")

        // Staying off-route does NOT re-latch every frame.
        m.push(GeoPoint(40.0021, 71.0006), null, null, 11_000L)
        assertFalse(m.consumeOffRouteSignal(), "staying off-route does not re-fire the signal")
    }

    @Test
    fun offRouteUsesEnterExitHysteresis() {
        // enter at 30m, exit at 15m: a fix between 15 and 30 should NOT flip state either way.
        val config = RouteFollowingConfig(offRouteEnterMeters = 30.0, offRouteExitMeters = 15.0)
        val m = routeModel(config)
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        assertEquals(RouteState.ON_ROUTE, m.routeState())

        // ~22m north of the east leg: inside the enter band -> still ON_ROUTE (no premature flip).
        val twentyTwoM = GeoPoint(40.0 + 0.0002, 71.0005)
        m.push(twentyTwoM, null, null, 10_000L)
        assertEquals(RouteState.ON_ROUTE, m.routeState(), "22m (<30 enter) stays ON_ROUTE")

        // Clearly off (~200m) -> OFF_ROUTE.
        m.push(GeoPoint(40.002, 71.0005), null, null, 20_000L)
        assertEquals(RouteState.OFF_ROUTE, m.routeState())

        // Back to ~22m: still > 15m exit threshold -> remains OFF_ROUTE (hysteresis holds).
        m.push(twentyTwoM, null, null, 30_000L)
        assertEquals(RouteState.OFF_ROUTE, m.routeState(), "22m (>15 exit) stays OFF_ROUTE until well back on")

        // Now genuinely back on the line (<15m) -> ON_ROUTE.
        m.push(GeoPoint(40.0, 71.0009), null, null, 40_000L)
        assertEquals(RouteState.ON_ROUTE, m.routeState(), "back within exit threshold -> ON_ROUTE")
    }

    @Test
    fun offRouteSignalRespectsRefetchCooldown() {
        val m = routeModel(RouteFollowingConfig(refetchCooldownMillis = 30_000L))
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        m.push(GeoPoint(40.002, 71.0005), null, null, 10_000L)
        assertTrue(m.consumeOffRouteSignal(), "first off-route crossing signals")

        // Recover and go off again within the cooldown window -> suppressed.
        m.push(GeoPoint(40.0, 71.0009), null, null, 12_000L)
        m.push(GeoPoint(40.002, 71.0005), null, null, 20_000L)
        assertFalse(m.consumeOffRouteSignal(), "second crossing within cooldown is suppressed")

        // After the cooldown elapses, a fresh crossing signals again.
        m.push(GeoPoint(40.0, 71.0009), null, null, 50_000L)
        m.push(GeoPoint(40.002, 71.0005), null, null, 60_000L)
        assertTrue(m.consumeOffRouteSignal(), "crossing after cooldown signals again")
    }

    @Test
    fun connectorLineDrawsFromRawGpsToSnappedPointWhileOnRoute() {
        val config = RouteFollowingConfig(connectorHideThreshold = 1.0)
        val m = routeModel(config)
        m.setRoute(route)
        // A fix ~10m off the line but within the snap threshold -> on-route, connector visible.
        val raw = GeoPoint(40.0 + 0.00009, 71.0005)
        m.push(raw, null, null, 0L)
        val connector = m.connector(0L)
        assertTrue(connector != null, "an on-route-but-offset fix yields a connector line")
        assertTrue(connector!!.rawPoint.distanceTo(raw) < 0.5, "connector starts at the raw GPS point")
        assertTrue(
            connector.snappedPoint.distanceTo(m.sample(0L).point) < 1.0,
            "connector ends at the snapped (drawn car) point"
        )
    }

    @Test
    fun connectorHidesWhenRawSitsOnTheLine() {
        val config = RouteFollowingConfig(connectorHideThreshold = 2.0)
        val m = routeModel(config)
        m.setRoute(route)
        // A fix essentially on the line (<2m) -> no connector clutter.
        m.push(GeoPoint(40.0, 71.0005), null, null, 0L)
        assertNull(m.connector(0L), "raw on the line -> connector hidden")
    }

    // ----- remainingRoute eaten in the model (edit #4). -----

    @Test
    fun remainingRouteShrinksAsTheCarAdvances() {
        val m = routeModel()
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0001), null, null, 0L)
        m.push(GeoPoint(40.0, 71.0009), null, null, 10_000L)

        fun remainingMeters(atMillis: Long): Double {
            val pts = m.remainingRoute(atMillis)
            var sum = 0.0
            for (i in 1 until pts.size) sum += pts[i - 1].distanceTo(pts[i])
            return sum
        }

        val early = remainingMeters(10_000L)
        val late = remainingMeters(20_000L)
        assertTrue(early > late, "remaining route shrinks as car advances: $early > $late")
    }

    @Test
    fun clearingRouteFallsBackToChordBehavior() {
        val m = routeModel()
        m.setRoute(route)
        m.push(GeoPoint(40.0, 71.0002), null, null, 0L)
        assertTrue(m.isFollowingRoute())
        m.setRoute(null)
        assertFalse(m.isFollowingRoute(), "clearing route exits route mode")
        val b = GeoPoint(40.001, 71.0)
        m.push(b, null, null, 10_000L)
        val mid = m.sample(15_000L).point
        assertTrue(mid.lat > 40.0 && mid.lat < b.lat, "chord-interpolating after clear: ${mid.lat}")
    }
}
