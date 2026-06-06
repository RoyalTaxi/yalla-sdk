package uz.yalla.core.payment

import uz.yalla.core.identity.CardId

data class PaymentCard(
    val cardId: CardId,
    val maskedPan: String
) {
    fun toPaymentMethod() = PaymentMethod.Card(
        cardId = cardId,
        maskedNumber = maskedPan
    )
}
