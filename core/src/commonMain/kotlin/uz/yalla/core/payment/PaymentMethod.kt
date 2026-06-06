package uz.yalla.core.payment

import uz.yalla.core.identity.CardId
import uz.yalla.core.util.normalizedId

sealed class PaymentMethod(val id: String) {
    data object Cash : PaymentMethod("cash")

    data class Card(val cardId: CardId, val maskedNumber: String) : PaymentMethod("card")

    companion object {
        fun from(
            id: String?,
            cardId: CardId? = null,
            maskedNumber: String? = null
        ): PaymentMethod = when (id.normalizedId()) {
            "card" -> {
                val normalizedRaw = cardId?.raw?.trim().orEmpty()
                if (normalizedRaw.isBlank()) Cash
                else Card(
                    cardId = CardId(normalizedRaw),
                    maskedNumber = maskedNumber?.trim().orEmpty()
                )
            }

            else -> Cash
        }
    }
}
