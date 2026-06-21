package uz.yalla.core.payment

import uz.yalla.core.identity.CardId

public data class PaymentCard(
    val cardId: CardId,
    val maskedPan: String
) {
    public fun toPaymentMethod(): PaymentMethod.Card =
        PaymentMethod.Card(
            cardId = cardId,
            maskedNumber = maskedPan
        )

    // Redacted so the masked PAN never lands in a log line or crash report (CWE-532).
    override fun toString(): String = "PaymentCard(cardId=$cardId, <redacted>)"
}
