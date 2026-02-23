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
        fun from(id: String?): PaymentKind =
            when (id?.lowercase()) {
                "cash" -> Cash
                // Card must be constructed directly with Card(cardId, maskedNumber)
                else -> Cash
            }
    }
}
