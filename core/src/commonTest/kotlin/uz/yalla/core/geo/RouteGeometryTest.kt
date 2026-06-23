package uz.yalla.core.geo

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RouteGeometryTest {
    private fun assertBearingClose(
        expected: Float,
        actual: Float?,
        eps: Float = 3f
    ) {
        if (actual == null) {
            assertTrue(false, "expected a bearing near $expected, got null")
            return
        }
        val delta = abs(((actual - expected + 540f) % 360f) - 180f)
        assertTrue(delta <= eps, "expected bearing~=$expected got $actual")
    }

    @Test
    fun nullWhenRouteHasFewerThanTwoPoints() {
        assertNull(headingAlongRoute(GeoPoint(41.0, 69.0), emptyList()))
        assertNull(headingAlongRoute(GeoPoint(41.0, 69.0), listOf(GeoPoint(41.0, 69.0))))
    }

    @Test
    fun snapsToEastwardSegment() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        assertBearingClose(90f, headingAlongRoute(GeoPoint(41.00002, 69.005), route))
    }

    @Test
    fun snapsToNorthwardSegment() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.01, 69.0))
        assertBearingClose(0f, headingAlongRoute(GeoPoint(41.005, 69.00002), route))
    }

    @Test
    fun nullWhenPointIsFarOffRoute() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        assertNull(headingAlongRoute(GeoPoint(41.05, 69.005), route))
    }

    @Test
    fun picksNearestSegmentOfAnLShapedRoute() {
        val route =
            listOf(
                GeoPoint(41.0, 69.0),
                GeoPoint(41.0, 69.01),
                GeoPoint(41.01, 69.01)
            )
        assertBearingClose(0f, headingAlongRoute(GeoPoint(41.005, 69.0100), route))
        assertBearingClose(90f, headingAlongRoute(GeoPoint(41.0, 69.005), route))
    }

    @Test
    fun skipsDuplicatedConsecutiveVertex() {
        val withDup =
            listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val withoutDup = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val query = GeoPoint(41.00002, 69.005)
        assertEquals(
            headingAlongRoute(query, withoutDup),
            headingAlongRoute(query, withDup)
        )
    }

    @Test
    fun nullWhenEveryRoutePointIsIdentical() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.0))
        assertNull(headingAlongRoute(GeoPoint(41.0, 69.0), route))
    }

    @Test
    fun includesPointJustInsideTheDefaultBoundaryAndExcludesJustOutside() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        assertBearingClose(90f, headingAlongRoute(GeoPoint(41.00039, 69.005), route))
        assertNull(headingAlongRoute(GeoPoint(41.00042, 69.005), route))
    }

    @Test
    fun honorsAnExplicitMaxOffRouteMeters() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val query = GeoPoint(41.001, 69.005)
        assertNull(headingAlongRoute(query, route))
        assertBearingClose(90f, headingAlongRoute(query, route, maxOffRouteMeters = 200.0))
    }

    @Test
    fun snappedPointProjectsNearbyGpsDriftOntoRoute() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val snapped = snappedPointOnRoute(GeoPoint(41.00002, 69.005), route) ?: error("expected a snapped point")
        assertClose(41.0, snapped.lat)
        assertClose(69.005, snapped.lng)
    }

    @Test
    fun pointNearRouteOnlyWhenWithinBoundary() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        assertTrue(isPointNearRoute(GeoPoint(41.00002, 69.005), route))
        assertTrue(!isPointNearRoute(GeoPoint(41.002, 69.005), route))
    }

    @Test
    fun remainingRouteDropsOnlyThePartAlreadyPassed() {
        val route =
            listOf(
                GeoPoint(41.0, 69.0),
                GeoPoint(41.0, 69.01),
                GeoPoint(41.01, 69.01)
            )
        val progress = routeProgress(GeoPoint(41.00002, 69.005), route)
        val remaining = progress.remainingRoute
        assertEquals(RouteProgressKind.ON_ROUTE, progress.kind)
        assertBearingClose(90f, progress.heading)
        assertClose(41.0, progress.displayPoint.lat)
        assertClose(69.005, progress.displayPoint.lng)
        assertEquals(3, remaining.size)
        assertClose(41.0, remaining[0].lat)
        assertClose(69.005, remaining[0].lng)
        assertEquals(route[1], remaining[1])
        assertEquals(route[2], remaining[2])
    }

    @Test
    fun remainingRouteClearsWhenDriverPassedTheEndAlongRoute() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val progress = routeProgress(GeoPoint(41.0, 69.012), route)
        assertEquals(RouteProgressKind.PASSED_ROUTE, progress.kind)
        assertEquals(emptyList(), progress.remainingRoute)
    }

    @Test
    fun remainingRouteDoesNotClearWhenDriverIsFarOffRoute() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val progress = routeProgress(GeoPoint(41.01, 69.012), route)
        assertEquals(RouteProgressKind.OFF_ROUTE, progress.kind)
        assertEquals(route, progress.remainingRoute)
        assertEquals(GeoPoint(41.01, 69.012), progress.displayPoint)
        assertNull(progress.heading)
    }

    private fun assertClose(
        expected: Double,
        actual: Double,
        eps: Double = 1e-8
    ) {
        assertTrue(abs(expected - actual) <= eps, "expected~=$expected got $actual")
    }
}
