package uz.yalla.core.payment

/**
 * A saved payment card from the user's wallet.
 *
 * @property cardId Unique card identifier from payment provider
 * @property maskedPan Masked card number for display
 * @since 0.0.1
 */
data class PaymentCard(
    val cardId: String,
    val maskedPan: String
) {
    /**
     * Converts this saved card to a [PaymentKind.Card] for use in order creation.
     *
     * @return A [PaymentKind.Card] with this card's ID and masked PAN
     * @see PaymentKind.Card
     */
    fun toPaymentType() =
        PaymentKind.Card(
            cardId = cardId,
            maskedNumber = maskedPan
        )
}
