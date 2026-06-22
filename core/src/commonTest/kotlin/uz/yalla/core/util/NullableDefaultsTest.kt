package uz.yalla.core.util

import kotlin.test.Test
import kotlin.test.assertEquals

class NullableDefaultsTest {
    @Test
    fun or0DefaultsNullToTypedZero() {
        assertEquals(0, (null as Int?).or0())
        assertEquals(0L, (null as Long?).or0())
        assertEquals(0f, (null as Float?).or0())
        assertEquals(0.0, (null as Double?).or0())
    }

    @Test
    fun or0PassesNonNullThrough() {
        assertEquals(5, (5 as Int?).or0())
        assertEquals(5L, (5L as Long?).or0())
        assertEquals(5f, (5f as Float?).or0())
        assertEquals(5.0, (5.0 as Double?).or0())
    }

    @Test
    fun orFalseDefaultsNullToFalseAndPassesThrough() {
        assertEquals(false, (null as Boolean?).orFalse())
        assertEquals(true, (true as Boolean?).orFalse())
        assertEquals(false, (false as Boolean?).orFalse())
    }
}
