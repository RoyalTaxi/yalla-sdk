package uz.yalla.core.payment

import uz.yalla.core.identity.CardId
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins [PaymentCard.toPaymentMethod] — the payment-critical-path conversion that remaps
 * `maskedPan` -> `maskedNumber` and carries the [CardId] verbatim. The field-name remap is exactly
 * the silent swap a copy-paste introduces, so it is asserted directly.
 */
class PaymentCardTest {
    @Test
    fun toPaymentMethodCarriesCardIdAndRemapsMaskedPanToMaskedNumber() {
        val card = PaymentCard(cardId = CardId("8600"), maskedPan = "8600 **** **** 1234")

        assertEquals(
            PaymentMethod.Card(cardId = CardId("8600"), maskedNumber = "8600 **** **** 1234"),
            card.toPaymentMethod()
        )
    }
}
