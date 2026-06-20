package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Output-based characterization of [formatMoney].
 *
 * The edges that bite: the sign placement (`-` prefixed once, ahead of the grouping),
 * the 3-digit space grouping boundaries (no leading separator at exactly 1 000),
 * and the null receiver collapsing to "0". This is user-facing currency text, so a silent
 * grouping or sign regression is high cost.
 */
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
        // No leading separator: exactly four digits groups as "1 000", not " 1 000".
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
        // KNOWN WART, pinned as-is (characterization, not endorsement): `-this` on Long.MIN_VALUE
        // overflows back to Long.MIN_VALUE, so the magnitude string keeps its own '-', the grouping
        // counts that '-' as a character, and the sign prefix then doubles it. Result is a double
        // dash. Money is never realistically Long.MIN_VALUE, so this is documented rather than fixed
        // here — fixing it is a behavior change that belongs in its own item.
        assertEquals("--9 223 372 036 854 775 808", Long.MIN_VALUE.formatMoney())
    }
}
