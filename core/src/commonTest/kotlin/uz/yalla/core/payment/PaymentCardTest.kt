package uz.yalla.core.payment

import uz.yalla.core.identity.CardId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PaymentCardTest {
    @Test
    fun shouldMapPaymentCardToPaymentKindCard() {
        val paymentCard =
            PaymentCard(
                cardId = CardId("card-1"),
                maskedPan = "8600 **** 0001"
            )

        val paymentKind = paymentCard.toPaymentType()

        val card = assertIs<PaymentKind.Card>(paymentKind)
        assertEquals(CardId("card-1"), card.cardId)
        assertEquals("8600 **** 0001", card.maskedNumber)
    }
}
