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
        assertBearingClose(90f, headingAlongRoute(GeoPoint(41.00053, 69.005), route))
        assertNull(headingAlongRoute(GeoPoint(41.00055, 69.005), route))
    }

    @Test
    fun honorsAnExplicitMaxOffRouteMeters() {
        val route = listOf(GeoPoint(41.0, 69.0), GeoPoint(41.0, 69.01))
        val query = GeoPoint(41.001, 69.005)
        assertNull(headingAlongRoute(query, route))
        assertBearingClose(90f, headingAlongRoute(query, route, maxOffRouteMeters = 200.0))
    }
}
