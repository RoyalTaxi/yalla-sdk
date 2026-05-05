package uz.yalla.composites.util

import uz.yalla.core.identity.CardId
import uz.yalla.core.payment.PaymentKind
import uz.yalla.resources.Res
import uz.yalla.resources.payment_card_humo_format
import uz.yalla.resources.payment_card_uzcard_format
import uz.yalla.resources.payment_type_cash
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Resource-resolution behavior for [PaymentKind.getStringResource].
 *
 * Card-brand selection rule: 16-digit `cardId.raw` → Humo; otherwise →
 * Uzcard. Verified by directly invoking the non-composable
 * `getStringResource()` extension.
 */
class PaymentResourceTest {
    @Test
    fun cash_resolves_to_payment_type_cash() {
        assertEquals(
            Res.string.payment_type_cash,
            PaymentKind.Cash.getStringResource()
        )
    }

    @Test
    fun card_with_16_digit_id_resolves_to_humo() {
        val card = PaymentKind.Card(cardId = CardId("1234567890123456"), maskedNumber = "**** 3456")
        assertEquals(
            Res.string.payment_card_humo_format,
            card.getStringResource()
        )
    }

    @Test
    fun card_with_15_digit_id_resolves_to_uzcard() {
        val card = PaymentKind.Card(cardId = CardId("123456789012345"), maskedNumber = "**** 2345")
        assertEquals(
            Res.string.payment_card_uzcard_format,
            card.getStringResource()
        )
    }

    @Test
    fun card_with_8_digit_id_resolves_to_uzcard() {
        val card = PaymentKind.Card(cardId = CardId("12345678"), maskedNumber = "**** 5678")
        assertEquals(
            Res.string.payment_card_uzcard_format,
            card.getStringResource()
        )
    }

    @Test
    fun card_with_empty_id_resolves_to_uzcard() {
        val card = PaymentKind.Card(cardId = CardId(""), maskedNumber = "")
        assertEquals(
            Res.string.payment_card_uzcard_format,
            card.getStringResource()
        )
    }
}
