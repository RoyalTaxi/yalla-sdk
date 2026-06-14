package uz.yalla.core

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.geo.distanceTo
import uz.yalla.core.order.OrderStatus
import uz.yalla.core.util.MaskFormatter
import uz.yalla.core.util.formatMoney
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Proves the shared KMP test convention is live for :core — `kotlin("test")` resolves and
 * `commonTest` runs under `testAndroidHostTest`. Characterizes a thin slice of the highest-value
 * pure functions so the wiring change ships with a real net rather than an empty source set.
 * The exhaustive coverage of the pure core lives in its own item.
 */
class ConventionSmokeTest {
    @Test
    fun orderStatus_from_decodesKnownAndUnknownIds() {
        assertEquals(OrderStatus.Completed, OrderStatus.from("completed"))
        assertEquals(OrderStatus.InProgress, OrderStatus.from("in_fetters"))
        assertEquals(OrderStatus.Appointed, OrderStatus.from("  APPOINTED  "))
        assertEquals(OrderStatus.Unknown("null"), OrderStatus.from(null))
        assertEquals(OrderStatus.Unknown("???"), OrderStatus.from("???"))
    }

    @Test
    fun formatMoney_groupsThousandsAndKeepsSign() {
        assertEquals("0", 0L.formatMoney())
        assertEquals("1 000", 1_000L.formatMoney())
        assertEquals("1 234 567", 1_234_567L.formatMoney())
        assertEquals("-12 000", (-12_000L).formatMoney())
        assertEquals("0", (null as Long?).formatMoney())
    }

    @Test
    fun maskFormatter_fillsUnderscoresAndStopsWhenTextRunsOut() {
        assertEquals("+998 90", MaskFormatter.format("99890", "+___ __ ___ __ __"))
        assertEquals("", MaskFormatter.format("", "+___"))
    }

    @Test
    fun geoPoint_distanceTo_isZeroForSamePointAndPositiveOtherwise() {
        val tashkent = GeoPoint(41.2995, 69.2401)
        val samarkand = GeoPoint(39.6270, 66.9750)
        assertEquals(0.0, tashkent.distanceTo(tashkent))
        assertTrue(tashkent.distanceTo(samarkand) > 250_000.0)
    }
}
