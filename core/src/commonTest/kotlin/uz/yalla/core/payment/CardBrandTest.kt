package uz.yalla.core.payment

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of [CardBrand.of].
 *
 * The whole decision is a single length check: exactly 16 characters -> [CardBrand.Humo],
 * everything else -> [CardBrand.Uzcard]. This is purely structural (it counts characters, it does
 * not validate digits), so the boundary at 16 and the default-to-Uzcard behavior are what matter.
 */
class CardBrandTest {

    @Test
    fun sixteenCharacterIdsAreHumo() {
        assertEquals(CardBrand.Humo, CardBrand.of("9860000000000000"))
        assertEquals(CardBrand.Humo, CardBrand.of("1234567890123456"))
    }

    @Test
    fun anyOtherLengthIsUzcard() {
        assertEquals(CardBrand.Uzcard, CardBrand.of("860000000000000")) // 15
        assertEquals(CardBrand.Uzcard, CardBrand.of("86000000000000000")) // 17
        assertEquals(CardBrand.Uzcard, CardBrand.of(""))
        assertEquals(CardBrand.Uzcard, CardBrand.of("8600"))
    }

    @Test
    fun checkIsByLengthNotContent() {
        // Sixteen non-digit characters still classify as Humo; `of` never inspects the characters.
        assertEquals(CardBrand.Humo, CardBrand.of("abcdefghijklmnop"))
    }

    @Test
    fun boundaryAtSixteenIsExact() {
        assertEquals(CardBrand.Uzcard, CardBrand.of("a".repeat(15)))
        assertEquals(CardBrand.Humo, CardBrand.of("a".repeat(16)))
        assertEquals(CardBrand.Uzcard, CardBrand.of("a".repeat(17)))
    }
}
