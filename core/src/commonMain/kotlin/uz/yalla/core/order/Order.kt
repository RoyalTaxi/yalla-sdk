package uz.yalla.core.order

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.identity.DriverId
import uz.yalla.core.identity.OrderId
import uz.yalla.core.payment.PaymentMethod

/**
 * One order envelope spanning every service the backend offers.
 *
 * The wire sends a single order row with a [service] discriminator and **nullable per-service
 * payloads** ([taxi], [intercity]). This domain type mirrors that shape verbatim — flat nullable
 * siblings, not a sealed hierarchy — because the backend does not guarantee a discriminated decode
 * and a taxi order and an intercity order share the same envelope (id, status, driver, payment).
 *
 * Exactly one payload is expected to be non-null, selected by [service]:
 * - `service == "taxi"` / `"road"` → [taxi] non-null, [intercity] null.
 * - `service == "intercity"` / `"postal"` → [intercity] non-null, [taxi] null.
 *
 * Consumers in a single-service context (the taxi feature, the history screens) should read their
 * own payload via [requireTaxi] when they are guaranteed taxi-shaped, or [isTaxi] / [isIntercity]
 * to branch defensively.
 *
 * @property id stable order identifier.
 * @property service raw service discriminator from the wire (kept as a `String`, not an enum, to
 *   stay faithful to the backend which can introduce new services without a client release).
 * @property status lenient order status (`Unknown` when the wire string is unrecognized).
 * @property statusTime transition timestamps for each status the order has passed through.
 * @property comment free-text rider comment.
 * @property dateTime order creation epoch (seconds).
 * @property paymentMethod cash or card.
 * @property driver the assigned executor; zero-valued when no driver is appointed yet.
 * @property number human-facing order number (`null` until assigned).
 * @property tariff canonical tariff `{id, name}` (supersedes the deprecated [Taxi.tariff] string).
 * @property routes canonical stops for the order (supersedes the deprecated [Taxi.points]).
 * @property options selected paid options across services.
 * @property track driver GPS breadcrumb, used by the live navigator; empty when absent.
 * @property taxi taxi per-service payload; `null` for non-taxi orders.
 * @property intercity intercity/postal per-service payload; `null` for non-intercity orders.
 */
public data class Order(
    val id: OrderId,
    val service: String,
    val status: OrderStatus,
    val statusTime: List<StatusTime>,
    val comment: String,
    val dateTime: Long,
    val paymentMethod: PaymentMethod,
    val driver: Driver,
    val number: Long? = null,
    val tariff: Tariff? = null,
    val routes: List<Taxi.Point> = emptyList(),
    val options: List<Option> = emptyList(),
    val track: List<Track> = emptyList(),
    val taxi: Taxi? = null,
    val intercity: Intercity? = null
) {
    /** `true` when this order carries a taxi payload. */
    val isTaxi: Boolean get() = taxi != null

    /** `true` when this order carries an intercity (or postal) payload. */
    val isIntercity: Boolean get() = intercity != null

    /**
     * Returns the [taxi] payload, asserting it is present.
     *
     * Call this only from a taxi-only context (the taxi feature, taxi history). It fails loudly
     * rather than silently mis-handling a non-taxi order — surfacing a malformed envelope at the
     * deref site instead of producing a zero-valued taxi.
     *
     * @throws IllegalStateException when this order has no taxi payload.
     */
    public fun requireTaxi(): Taxi = taxi ?: error("Order $id has no taxi payload (service=$service)")

    public data class Driver(
        val point: GeoPoint,
        val heading: Double,
        val vehicle: Vehicle,
        val fatherName: String,
        val givenNames: String,
        val id: DriverId,
        val phone: String,
        val photo: String,
        val rating: Double,
        val surName: String
    ) {
        override fun toString(): String = "Driver(id=$id, <redacted>)"

        public data class Vehicle(
            val callsign: String,
            val color: Color,
            val id: Int,
            val mark: String,
            val model: String,
            val stateNumber: String
        ) {
            public data class Color(
                val hex: String,
                val name: String
            )
        }
    }

    public data class StatusTime(
        val status: OrderStatus,
        val time: Long
    )

    /** Canonical tariff identity carried at the envelope level. */
    public data class Tariff(
        val id: Int,
        val name: String
    )

    /** A selected paid option (extra service chosen at order time). */
    public data class Option(
        val id: Int,
        val cost: Int,
        val costType: String,
        val name: String
    )

    /** A single driver-location breadcrumb sample. */
    public data class Track(
        val accuracy: Double,
        val point: GeoPoint,
        val locationType: String,
        val online: Boolean,
        val speed: Double,
        val status: String,
        val time: Long
    )

    public data class Taxi(
        val bonusAmount: Int,
        val clientTotalPrice: Double,
        val distance: Double,
        val fixedPrice: Boolean,
        val points: List<Point>,
        val services: List<ExtraService>,
        val startPrice: Int,
        val tariff: String,
        val tariffId: Int,
        val totalPrice: Int,
        val waitingTime: Int
    ) {
        public data class Point(
            val coords: GeoPoint,
            val fullAddress: String,
            val index: Int
        )
    }

    /**
     * Intercity / postal per-service payload.
     *
     * Mirrors the colleague's `OrderDetails.OrderIntercity`. All scalar fields stay nullable to
     * match the wire; the booleans default to `false` and [seatLayouts] to empty so call sites do
     * not null-check structural collections.
     */
    public data class Intercity(
        val startHour: String?,
        val endHour: String?,
        val scheduleId: Int?,
        val totalPrice: Double?,
        val isBooked: Boolean,
        val isPostal: Boolean,
        val seatLayouts: List<SeatLayout>
    ) {
        public data class SeatLayout(
            val slug: String,
            val index: Int,
            val seatLayoutId: Int,
            val price: Double
        )
    }
}
