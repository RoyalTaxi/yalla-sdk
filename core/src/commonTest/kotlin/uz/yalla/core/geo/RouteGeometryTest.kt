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
        assertTrue(actual != null, "expected a bearing near $expected, got null")
        val delta = abs(((actual!! - expected + 540f) % 360f) - 180f)
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
        // A route with a repeated point (common in backend polylines) must behave like the
        // de-duplicated route — exercises the `if (a == b) continue` guard.
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
        // All-duplicate route: every segment is skipped, so no segment is ever selected -> null.
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.0))
        assertNull(headingAlongRoute(GeoPoint(41.0, 69.0), route))
    }

    @Test
    fun includesPointJustInsideTheDefaultBoundaryAndExcludesJustOutside() {
        // East-west segment at lat 41; a meridian degree is ~111_195 m, so ~0.00053 deg ~= 59 m and
        // ~0.00055 deg ~= 61 m of north offset straddle the default 60 m threshold.
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        assertBearingClose(90f, headingAlongRoute(GeoPoint(41.00053, 69.005), route))
        assertNull(headingAlongRoute(GeoPoint(41.00055, 69.005), route))
    }

    @Test
    fun honorsAnExplicitMaxOffRouteMeters() {
        // A point ~111 m off-route is rejected by the default 60 m but accepted at a 200 m threshold.
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val query = GeoPoint(41.001, 69.005)
        assertNull(headingAlongRoute(query, route))
        assertBearingClose(90f, headingAlongRoute(query, route, maxOffRouteMeters = 200.0))
    }
}
