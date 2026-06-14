package uz.yalla.core.payment

import uz.yalla.core.identity.CardId
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Characterization of [PaymentMethod.from].
 *
 * The contract that is easy to break silently: "card" alone is NOT enough to produce a [Card] —
 * the decoder degrades to [Cash] whenever the card id is null/blank, and it trims the raw id and
 * masked number. So a malformed card payload can never surface a Card with an empty id; it always
 * falls back to Cash. Anything other than the normalized "card" id is Cash.
 */
class PaymentMethodTest {

    @Test
    fun anyNonCardIdDecodesToCash() {
        assertEquals(PaymentMethod.Cash, PaymentMethod.from("cash"))
        assertEquals(PaymentMethod.Cash, PaymentMethod.from(null))
        assertEquals(PaymentMethod.Cash, PaymentMethod.from(""))
        assertEquals(PaymentMethod.Cash, PaymentMethod.from("wallet"))
    }

    @Test
    fun cardIdIsNormalizedBeforeMatching() {
        assertEquals(
            PaymentMethod.Card(CardId("abc"), "1234"),
            PaymentMethod.from("  CARD  ", cardId = CardId("abc"), maskedNumber = "1234")
        )
    }

    @Test
    fun cardWithBlankOrNullCardIdDegradesToCash() {
        assertEquals(PaymentMethod.Cash, PaymentMethod.from("card", cardId = null))
        assertEquals(PaymentMethod.Cash, PaymentMethod.from("card", cardId = CardId("")))
        assertEquals(PaymentMethod.Cash, PaymentMethod.from("card", cardId = CardId("   ")))
    }

    @Test
    fun cardWithValidIdTrimsRawIdAndMaskedNumber() {
        assertEquals(
            PaymentMethod.Card(CardId("8600"), "8600 **** **** 1234"),
            PaymentMethod.from(
                "card",
                cardId = CardId("  8600  "),
                maskedNumber = "  8600 **** **** 1234  "
            )
        )
    }

    @Test
    fun cardWithNullMaskedNumberBecomesEmptyString() {
        assertEquals(
            PaymentMethod.Card(CardId("8600"), ""),
            PaymentMethod.from("card", cardId = CardId("8600"), maskedNumber = null)
        )
    }

    @Test
    fun idsAreStable() {
        assertEquals("cash", PaymentMethod.Cash.id)
        assertEquals("card", PaymentMethod.Card(CardId("x"), "y").id)
    }
}
