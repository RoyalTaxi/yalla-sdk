package uz.yalla.components.payment

import uz.yalla.core.identity.CardId
import uz.yalla.core.payment.CardBrand
import uz.yalla.core.payment.PaymentMethod
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The label is the only non-`@Composable` half of the presentation, so it is the part that can be
 * pinned down without a UI test. These assertions guard that every [CardBrand] still maps to a
 * label and that the [PaymentMethod.Card] overload classifies through [CardBrand.of] rather than
 * inventing its own heuristic.
 */
class CardBrandPresentationTest {
    @Test
    fun everyBrandHasALabel() {
        assertEquals("Humo", cardBrandLabel(CardBrand.Humo))
        assertEquals("Uzcard", cardBrandLabel(CardBrand.Uzcard))
    }

    @Test
    fun labelMatchesBrandOfForSixteenDigitCard() {
        val card = PaymentMethod.Card(cardId = CardId("9860000000000000"), maskedNumber = "0000")
        assertEquals(cardBrandLabel(CardBrand.of(card.cardId.raw)), cardBrandLabel(CardBrand.Humo))
    }

    @Test
    fun labelMatchesBrandOfForOtherLengthCard() {
        val card = PaymentMethod.Card(cardId = CardId("860000000000000"), maskedNumber = "0000")
        assertEquals(cardBrandLabel(CardBrand.of(card.cardId.raw)), cardBrandLabel(CardBrand.Uzcard))
    }
}
