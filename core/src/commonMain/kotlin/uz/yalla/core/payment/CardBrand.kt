package uz.yalla.core.payment

private const val HUMO_CARD_ID_LENGTH = 16

public enum class CardBrand {
    Humo,
    Uzcard;

    public companion object {
        public fun of(cardId: String): CardBrand = if (cardId.length == HUMO_CARD_ID_LENGTH) Humo else Uzcard
    }
}
