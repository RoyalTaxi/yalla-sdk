package uz.yalla.core.identity

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Typed identifier for an [uz.yalla.core.order.Order].
 *
 * Wraps the raw `Int` server identifier so it can't accidentally be passed
 * where an [ExecutorId] / [TaxiId] / etc. is expected. The wire format is
 * the bare integer; serializers go through the inline [raw] field.
 */
@Serializable
@JvmInline
value class OrderId(
    val raw: Int
)

/**
 * Typed identifier for an [uz.yalla.core.order.Executor]. Wire format: bare integer.
 */
@Serializable
@JvmInline
value class ExecutorId(
    val raw: Int
)

/**
 * Typed identifier for an [uz.yalla.core.order.Order.Taxi]. Wire format: bare integer.
 */
@Serializable
@JvmInline
value class TaxiId(
    val raw: Int
)

/**
 * Typed identifier for an [uz.yalla.core.order.ExtraService]. Wire format: bare integer.
 */
@Serializable
@JvmInline
value class ExtraServiceId(
    val raw: Int
)

/**
 * Typed identifier for an [uz.yalla.core.order.ServiceBrand]. Wire format: bare integer.
 */
@Serializable
@JvmInline
value class ServiceBrandId(
    val raw: Int
)

/**
 * Typed identifier for an [uz.yalla.core.location.Address].
 *
 * Often nullable on the wire (a freshly geocoded address has no database row
 * yet). The serializer round-trips `null` cleanly through `AddressId?` per the
 * test in `SerializationRoundTripTest.shouldRoundTripAddress`.
 *
 * Wire format: bare integer (or `null`).
 */
@Serializable
@JvmInline
value class AddressId(
    val raw: Int
)

/**
 * Typed identifier for an [uz.yalla.core.location.AddressOption]. Wire format: bare integer.
 */
@Serializable
@JvmInline
value class AddressOptionId(
    val raw: Int
)

/**
 * Typed identifier for a saved payment card.
 *
 * Wraps the server-issued opaque token. Wire format: bare string.
 *
 * @see uz.yalla.core.payment.PaymentCard
 * @see uz.yalla.core.payment.PaymentKind.Card
 */
@Serializable
@JvmInline
value class CardId(
    val raw: String
)
