package uz.yalla.core.kind

sealed class PaymentKind(
    val id: String
) {
    data object Cash : PaymentKind("cash")

    data class Card(
        val cardId: String,
        val maskedNumber: String
    ) : PaymentKind("card")

    companion object {
        fun from(id: String?, cardId: String? = null, maskedNumber: String? = null): PaymentKind =
            when (id?.lowercase()) {
                "cash" -> Cash
                "card" -> Card(cardId = cardId.orEmpty(), maskedNumber = maskedNumber.orEmpty())
                else -> Cash
            }
    }
}
