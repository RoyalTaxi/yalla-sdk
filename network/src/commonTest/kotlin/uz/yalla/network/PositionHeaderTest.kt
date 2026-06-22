package uz.yalla.network

import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertEquals

class PositionHeaderTest {
    @Test
    fun formatsToSixDecimalsWithASingleSpaceSeparator() {
        assertEquals("41.311081 69.240562", formatPosition(GeoPoint(41.311081, 69.240562)))
    }

    @Test
    fun padsTrailingZerosToFixedPrecision() {
        assertEquals("1.500000 2.000000", formatPosition(GeoPoint(1.5, 2.0)))
    }

    @Test
    fun roundsBeyondSixDecimals() {
        assertEquals("0.123457 -0.000001", formatPosition(GeoPoint(0.1234567, -0.0000005)))
    }

    @Test
    fun keepsNegativeSignAndDropsItForZero() {
        assertEquals("-1.000000 0.000000", formatPosition(GeoPoint(-1.0, 0.0)))
        assertEquals("0.000000 0.000000", formatPosition(GeoPoint(-0.0, 0.0)))
    }
}
