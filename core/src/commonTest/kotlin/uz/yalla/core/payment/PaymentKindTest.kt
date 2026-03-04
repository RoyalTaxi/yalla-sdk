package uz.yalla.core.payment

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PaymentKindTest {
    @Test
    fun shouldReturnCashWhenIdIsCash() {
        val paymentKind = PaymentKind.from("cash")

        assertEquals(PaymentKind.Cash, paymentKind)
    }

    @Test
    fun shouldReturnCardWhenIdIsCardAndCardIdIsProvided() {
        val paymentKind =
            PaymentKind.from(
                id = "  CARD  ",
                cardId = "  card-123  ",
                maskedNumber = "  8600 **** 1234  "
            )

        val card = assertIs<PaymentKind.Card>(paymentKind)
        assertEquals("card-123", card.cardId)
        assertEquals("8600 **** 1234", card.maskedNumber)
    }

    @Test
    fun shouldReturnCashWhenCardIdIsBlank() {
        val paymentKind =
            PaymentKind.from(
                id = "card",
                cardId = "   ",
                maskedNumber = "8600 **** 1234"
            )

        assertEquals(PaymentKind.Cash, paymentKind)
    }

    @Test
    fun shouldReturnCashWhenCardIdIsNull() {
        val paymentKind =
            PaymentKind.from(
                id = "card",
                cardId = null,
                maskedNumber = "8600 **** 1234"
            )

        assertEquals(PaymentKind.Cash, paymentKind)
    }

    @Test
    fun shouldReturnCashWhenIdIsUnknown() {
        val paymentKind = PaymentKind.from("something_else")

        assertEquals(PaymentKind.Cash, paymentKind)
    }
}
