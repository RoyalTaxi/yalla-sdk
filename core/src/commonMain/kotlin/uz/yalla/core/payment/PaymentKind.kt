package uz.yalla.core.payment

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import uz.yalla.core.util.normalizedId

/**
 * Custom [KSerializer] for [PaymentKind].
 *
 * Encodes as the plain `String` [PaymentKind.id] (`"cash"` or `"card"`).
 * Deserialization calls [PaymentKind.Companion.from] with only the type tag — card-specific
 * fields ([PaymentKind.Card.cardId] / [PaymentKind.Card.maskedNumber]) are not recoverable
 * from the wire token alone and default to empty strings, which [from] maps to [PaymentKind.Cash].
 *
 * This serializer is appropriate for **preferences storage** where only the payment-type tag
 * needs to persist. Full card details are sourced separately from the payment provider.
 *
 * Wire format: a single JSON string (e.g. `"cash"`, `"card"`).
 *
 * @since 0.0.8
 */
object PaymentKindSerializer : KSerializer<PaymentKind> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PaymentKind", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PaymentKind) {
        encoder.encodeString(value.id)
    }

    override fun deserialize(decoder: Decoder): PaymentKind =
        PaymentKind.from(decoder.decodeString())
}

/**
 * Payment method for an order.
 *
 * Either [Cash] or [Card] with linked card details. Factory method [from]
 * handles API deserialization with fallback to [Cash] for unknown types.
 *
 * Serializes as a plain JSON string via [PaymentKindSerializer]. Note that [Card]-specific
 * fields are not represented in the wire token; see [PaymentKindSerializer] for the
 * documented trade-off.
 *
 * @property id Wire-format identifier ("cash" or "card")
 * @since 0.0.1
 */
@Serializable(with = PaymentKindSerializer::class)
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
         *
         * @param id Wire-format payment type ("cash" or "card"), or `null`
         * @param cardId Card identifier from payment provider, or `null`
         * @param maskedNumber Masked card PAN for display (e.g., "**** 1234"), or `null`
         * @return [Card] if id is "card" and cardId is non-blank, [Cash] otherwise
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
