package uz.yalla.core.geo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArchedPolylineTest {
    @Test
    fun `samples segments plus one points anchored at the endpoints`() {
        val from = GeoPoint(40.0, 71.0)
        val to = GeoPoint(41.0, 69.0)

        val arc = archedPolyline(from, to, segments = 10)

        assertEquals(11, arc.size)
        assertEquals(from, arc.first())
        assertEquals(to, arc.last())
    }

    @Test
    fun `bows off the straight chord at the midpoint`() {
        val from = GeoPoint(40.0, 71.0)
        val to = GeoPoint(40.0, 73.0)

        val midpoint = archedPolyline(from, to, curvature = 0.5, segments = 10)[5]

        assertTrue(midpoint.lat != 40.0, "the arch must deviate from the flat chord")
    }

    @Test
    fun `degenerate input returns the two endpoints`() {
        val point = GeoPoint(40.0, 71.0)

        assertEquals(listOf(point, point), archedPolyline(point, point))
    }
}
