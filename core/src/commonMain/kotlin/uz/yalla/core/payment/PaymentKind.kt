package uz.yalla.core.payment

import uz.yalla.core.util.normalizedId

/**
 * Payment method for an order.
 *
 * Either [Cash] or [Card] with linked card details. Factory method [from]
 * handles API deserialization with fallback to [Cash] for unknown types.
 *
 * @property id Wire-format identifier ("cash" or "card")
 * @since 0.0.1
 */
sealed class PaymentKind(val id: String) {
    /** Cash payment. */
    data object Cash : PaymentKind("cash")

    /**
     * Card payment with linked card details.
     *
     * @property cardId Unique card identifier from payment provider
     * @property maskedNumber Masked card PAN for display (e.g., "**** 1234")
     */
    data class Card(
        val cardId: String,
        val maskedNumber: String
    ) : PaymentKind("card")

    companion object {
        /**
         * Parses payment type from API response fields.
         *
         * Falls back to [Cash] if [id] is unrecognized or [cardId] is blank.
         */
        fun from(
            id: String?,
            cardId: String? = null,
            maskedNumber: String? = null
        ): PaymentKind =
            when (id.normalizedId()) {
                "cash" -> Cash
                "card" -> {
                    val normalizedCardId = cardId?.trim().orEmpty()
                    if (normalizedCardId.isBlank()) {
                        Cash
                    } else {
                        Card(
                            cardId = normalizedCardId,
                            maskedNumber = maskedNumber?.trim().orEmpty()
                        )
                    }
                }
                else -> Cash
            }
    }
}
