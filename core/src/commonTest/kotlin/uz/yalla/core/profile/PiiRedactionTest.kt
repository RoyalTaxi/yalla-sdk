package uz.yalla.core.profile

import uz.yalla.core.identity.CardId
import uz.yalla.core.payment.PaymentCard
import uz.yalla.core.payment.PaymentMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PiiRedactionTest {
    @Test
    fun clientToStringOmitsPhoneNameBirthdayAndBalance() {
        val client =
            Client(
                phone = "998901234567",
                name = "Alisher",
                surname = "Karimov",
                image = "https://img/me.png",
                birthday = "1990-01-02",
                balance = 5_000L,
                gender = GenderKind.Male
            )

        val rendered = client.toString()

        assertFalse(rendered.contains("998901234567"), "phone leaked: $rendered")
        assertFalse(rendered.contains("Alisher"), "name leaked: $rendered")
        assertFalse(rendered.contains("Karimov"), "surname leaked: $rendered")
        assertFalse(rendered.contains("1990-01-02"), "birthday leaked: $rendered")
        assertFalse(rendered.contains("5000"), "balance leaked: $rendered")
        assertTrue(rendered.contains("redacted"), "expected a redaction marker: $rendered")
    }

    @Test
    fun paymentMethodCardToStringOmitsMaskedNumber() {
        val card = PaymentMethod.Card(cardId = CardId("tok_abc"), maskedNumber = "8600 **** **** 1234")

        val rendered = card.toString()

        assertFalse(rendered.contains("8600 **** **** 1234"), "masked PAN leaked: $rendered")
        assertTrue(rendered.contains("redacted"), "expected a redaction marker: $rendered")
    }

    @Test
    fun paymentCardToStringOmitsMaskedPan() {
        val card = PaymentCard(cardId = CardId("tok_abc"), maskedPan = "9860 **** **** 4321")

        val rendered = card.toString()

        assertFalse(rendered.contains("9860 **** **** 4321"), "masked PAN leaked: $rendered")
        assertTrue(rendered.contains("redacted"), "expected a redaction marker: $rendered")
    }

    @Test
    fun redactionDoesNotAffectEquality() {
        val a = PaymentCard(cardId = CardId("tok"), maskedPan = "1111")
        val b = PaymentCard(cardId = CardId("tok"), maskedPan = "1111")
        assertEquals(a, b)
        assertEquals("1111", a.maskedPan)
    }
}
