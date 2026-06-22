package uz.yalla.core.payment

import uz.yalla.core.identity.CardId
import uz.yalla.core.util.normalizedId

public sealed class PaymentMethod(
    public val id: String
) {
    public data object Cash : PaymentMethod("cash")

    public data class Card(
        val cardId: CardId,
        val maskedNumber: String
    ) : PaymentMethod("card") {
        override fun toString(): String = "Card(cardId=$cardId, <redacted>)"
    }

    public companion object {
        public fun from(
            id: String?,
            cardId: CardId? = null,
            maskedNumber: String? = null
        ): PaymentMethod =
            when (id.normalizedId()) {
                "card" -> {
                    val normalizedRaw = cardId?.raw?.trim().orEmpty()
                    if (normalizedRaw.isBlank()) {
                        Cash
                    } else {
                        Card(
                            cardId = CardId(normalizedRaw),
                            maskedNumber = maskedNumber?.trim().orEmpty()
                        )
                    }
                }

                else -> Cash
            }
    }
}
