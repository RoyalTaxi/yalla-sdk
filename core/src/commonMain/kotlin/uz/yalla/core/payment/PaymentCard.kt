package uz.yalla.core.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A saved payment card from the user's wallet. */
@Serializable
data class PaymentCard(
    @SerialName("cardId") val cardId: String,
    @SerialName("maskedPan") val maskedPan: String
) {
    /**
     * Converts this saved card to a [PaymentKind.Card] for use in order creation.
     *
     * @see PaymentKind.Card
     */
    fun toPaymentType() =
        PaymentKind.Card(
            cardId = cardId,
            maskedNumber = maskedPan
        )
}
