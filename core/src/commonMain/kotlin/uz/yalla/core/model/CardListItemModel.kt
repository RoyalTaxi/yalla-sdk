package uz.yalla.core.model

import uz.yalla.core.kind.PaymentKind

data class CardListItemModel(
    val cardId: String,
    val maskedPan: String
) {
    fun toPaymentType() =
        PaymentKind.Card(
            cardId = cardId,
            maskedNumber = maskedPan
        )
}
