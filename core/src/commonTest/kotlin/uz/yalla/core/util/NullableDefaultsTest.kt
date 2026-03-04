package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NullableDefaultsTest {
    @Test
    fun shouldReturnZeroWhenIntIsNull() {
        val value: Int? = null

        assertEquals(0, value.or0())
    }

    @Test
    fun shouldReturnValueWhenIntIsNotNull() {
        val value: Int? = 7

        assertEquals(7, value.or0())
    }

    @Test
    fun shouldReturnZeroWhenLongIsNull() {
        val value: Long? = null

        assertEquals(0L, value.or0())
    }

    @Test
    fun shouldReturnValueWhenLongIsNotNull() {
        val value: Long? = 9L

        assertEquals(9L, value.or0())
    }

    @Test
    fun shouldReturnZeroWhenFloatIsNull() {
        val value: Float? = null

        assertEquals(0f, value.or0())
    }

    @Test
    fun shouldReturnValueWhenFloatIsNotNull() {
        val value: Float? = 12.5f

        assertEquals(12.5f, value.or0())
    }

    @Test
    fun shouldReturnZeroWhenDoubleIsNull() {
        val value: Double? = null

        assertEquals(0.0, value.or0())
    }

    @Test
    fun shouldReturnValueWhenDoubleIsNotNull() {
        val value: Double? = 15.25

        assertEquals(15.25, value.or0())
    }

    @Test
    fun shouldReturnFalseWhenBooleanIsNull() {
        val value: Boolean? = null

        assertFalse(value.orFalse())
    }

    @Test
    fun shouldReturnValueWhenBooleanIsNotNull() {
        val value: Boolean? = true

        assertTrue(value.orFalse())
    }

    @Test
    fun shouldFormatMoneyWithSpacesWhenLongIsPositive() {
        val formatted = 1_234_567L.formatMoney()

        assertEquals("1 234 567", formatted)
    }

    @Test
    fun shouldFormatMoneyWithMinusSignWhenLongIsNegative() {
        val formatted = (-2_345L).formatMoney()

        assertEquals("-2 345", formatted)
    }

    @Test
    fun shouldReturnZeroStringWhenNullableLongIsNull() {
        val value: Long? = null

        assertEquals("0", value.formatMoney())
    }

    @Test
    fun shouldFormatMoneyWhenNullableLongHasValue() {
        val value: Long? = 400_000L

        assertEquals("400 000", value.formatMoney())
    }
}
