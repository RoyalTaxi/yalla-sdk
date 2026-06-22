package uz.yalla.core.payment

import kotlin.test.Test
import kotlin.test.assertEquals

class CardBrandTest {
    @Test
    fun sixteenCharacterIdsAreHumo() {
        assertEquals(CardBrand.Humo, CardBrand.of("9860000000000000"))
        assertEquals(CardBrand.Humo, CardBrand.of("1234567890123456"))
    }

    @Test
    fun anyOtherLengthIsUzcard() {
        assertEquals(CardBrand.Uzcard, CardBrand.of("860000000000000"))
        assertEquals(CardBrand.Uzcard, CardBrand.of("86000000000000000"))
        assertEquals(CardBrand.Uzcard, CardBrand.of(""))
        assertEquals(CardBrand.Uzcard, CardBrand.of("8600"))
    }

    @Test
    fun checkIsByLengthNotContent() {
        assertEquals(CardBrand.Humo, CardBrand.of("abcdefghijklmnop"))
    }

    @Test
    fun boundaryAtSixteenIsExact() {
        assertEquals(CardBrand.Uzcard, CardBrand.of("a".repeat(15)))
        assertEquals(CardBrand.Humo, CardBrand.of("a".repeat(16)))
        assertEquals(CardBrand.Uzcard, CardBrand.of("a".repeat(17)))
    }
}
