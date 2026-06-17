package uz.yalla.core.geo

import kotlin.test.Test
import kotlin.test.assertEquals

class GeoSpacingTest {
    private fun pts(vararg p: GeoPoint): List<GeoPoint> = p.toList()

    @Test
    fun keepsAllWhenAllFartherApartThanMinimum() {
        val list =
            pts(
                GeoPoint(41.0, 69.0),
                GeoPoint(41.01, 69.0),
                GeoPoint(41.02, 69.0)
            )
        assertEquals(3, list.spacedApartBy(50.0) { it }.size)
    }

    @Test
    fun dropsPointsCloserThanMinimum() {
        val list =
            pts(
                GeoPoint(41.0, 69.0),
                GeoPoint(41.00005, 69.0),
                GeoPoint(41.0001, 69.0)
            )
        assertEquals(1, list.spacedApartBy(40.0) { it }.size)
    }

    @Test
    fun greedilyKeepsTheEarlierOfTwoClosePoints() {
        val first = GeoPoint(41.0, 69.0)
        val second = GeoPoint(41.00005, 69.0)
        val kept = pts(first, second).spacedApartBy(40.0) { it }
        assertEquals(listOf(first), kept)
    }

    @Test
    fun emptyStaysEmpty() {
        assertEquals(emptyList(), emptyList<GeoPoint>().spacedApartBy(40.0) { it })
    }
}
