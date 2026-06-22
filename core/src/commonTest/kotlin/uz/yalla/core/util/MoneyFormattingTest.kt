package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormattingTest {
    @Test
    fun formatsZero() {
        assertEquals("0", 0L.formatMoney())
    }

    @Test
    fun leavesSubThousandValuesUngrouped() {
        assertEquals("1", 1L.formatMoney())
        assertEquals("12", 12L.formatMoney())
        assertEquals("123", 123L.formatMoney())
        assertEquals("999", 999L.formatMoney())
    }

    @Test
    fun insertsSeparatorAtFirstThousandsBoundary() {
        assertEquals("1 000", 1_000L.formatMoney())
        assertEquals("9 999", 9_999L.formatMoney())
        assertEquals("10 000", 10_000L.formatMoney())
        assertEquals("100 000", 100_000L.formatMoney())
    }

    @Test
    fun groupsEveryThreeDigitsForLargeValues() {
        assertEquals("1 000 000", 1_000_000L.formatMoney())
        assertEquals("1 234 567", 1_234_567L.formatMoney())
        assertEquals("12 345 678", 12_345_678L.formatMoney())
        assertEquals("123 456 789", 123_456_789L.formatMoney())
    }

    @Test
    fun prefixesSignOnceAheadOfGrouping() {
        assertEquals("-1", (-1L).formatMoney())
        assertEquals("-999", (-999L).formatMoney())
        assertEquals("-1 000", (-1_000L).formatMoney())
        assertEquals("-12 000", (-12_000L).formatMoney())
        assertEquals("-1 234 567", (-1_234_567L).formatMoney())
    }

    @Test
    fun negativeAndPositiveDifferOnlyByThePrefix() {
        assertEquals("-" + 1_234_567L.formatMoney(), (-1_234_567L).formatMoney())
    }

    @Test
    fun nullReceiverCollapsesToZero() {
        assertEquals("0", (null as Long?).formatMoney())
    }

    @Test
    fun nonNullNullableReceiverFormatsLikeTheValue() {
        val value: Long? = 1_500_000L
        assertEquals("1 500 000", value.formatMoney())
    }

    @Test
    fun handlesLongMaxValue() {
        assertEquals("9 223 372 036 854 775 807", Long.MAX_VALUE.formatMoney())
    }

    @Test
    fun longMinValueProducesGarbledOutputKnownWart() {
        assertEquals("--9 223 372 036 854 775 808", Long.MIN_VALUE.formatMoney())
    }
}
