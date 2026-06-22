package uz.yalla.core.payment

import uz.yalla.core.identity.CardId
import kotlin.test.Test
import kotlin.test.assertEquals

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
