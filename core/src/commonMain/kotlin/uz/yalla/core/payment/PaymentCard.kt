package uz.yalla.core.payment

import uz.yalla.core.payment.PaymentKind

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
    /** Converts to [PaymentKind.Card] for use in order creation. */
    fun toPaymentType() =
        PaymentKind.Card(
            cardId = cardId,
            maskedNumber = maskedPan
        )
}
