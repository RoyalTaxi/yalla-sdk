package uz.yalla.core.payment

private const val HUMO_CARD_ID_LENGTH = 16

enum class CardBrand {
    Humo,
    Uzcard;

    companion object {
        fun of(cardId: String): CardBrand = if (cardId.length == HUMO_CARD_ID_LENGTH) Humo else Uzcard
    }
}
