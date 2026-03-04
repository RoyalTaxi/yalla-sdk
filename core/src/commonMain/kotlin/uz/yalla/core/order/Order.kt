package uz.yalla.core.order

import uz.yalla.core.payment.PaymentKind

/**
 * Complete taxi order aggregate containing all order details.
 *
 * This is the primary domain model returned from order-related API calls.
 * Contains nested types for executor info, vehicle details, route, and pricing.
 *
 * @property comment Passenger's note to the driver
 * @property dateTime Order creation timestamp (epoch millis)
 * @property executor Assigned driver details
 * @property id Unique order identifier
 * @property paymentType Selected payment method
 * @property service Service/tariff name
 * @property status Current order lifecycle state
 * @property statusTime History of status transitions with timestamps
 * @property taxi Route, pricing, and tariff details
 * @since 0.0.1
 */
data class Order(
    val comment: String,
    val dateTime: Long,
    val executor: Executor,
    val id: Int,
    val paymentType: PaymentKind,
    val service: String,
    val status: OrderStatus,
    val statusTime: List<StatusTime>,
    val taxi: Taxi
) {
    /** Assigned driver with contact info, location, and vehicle details. */
    data class Executor(
        val coords: Coords,
        val vehicle: Vehicle,
        val fatherName: String,
        val givenNames: String,
        val id: Int,
        val phone: String,
        val photo: String,
        val rating: Double,
        val surName: String
    ) {
        /** Real-time driver coordinates and heading. */
        data class Coords(
            val heading: Double,
            val lat: Double,
            val lng: Double
        )

        /** Driver's vehicle information. */
        data class Vehicle(
            val callsign: String,
            val color: Color,
            val id: Int,
            val mark: String,
            val model: String,
            val stateNumber: String
        ) {
            data class Color(
                val color: String,
                val name: String
            )
        }
    }

    /** Records when the order transitioned to a specific status. */
    data class StatusTime(
        val status: String,
        val time: Long
    )

    /** Route, pricing, and tariff details for the order. */
    data class Taxi(
        val bonusAmount: Int,
        val clientTotalPrice: Double,
        val distance: Double,
        val fixedPrice: Boolean,
        val routes: List<Route>,
        val services: List<ExtraService>,
        val startPrice: Int,
        val tariff: String,
        val tariffId: Int,
        val totalPrice: Int,
        val waitingTime: Int
    ) {
        /** A waypoint in the order's route. */
        data class Route(
            val coords: Coords,
            val fullAddress: String,
            val index: Int
        ) {
            data class Coords(
                val lat: Double,
                val lng: Double
            )
        }
    }
}

/** Converts this detailed executor to a lightweight [Executor] for map tracking. */
fun Order.Executor.toExecutor() =
    Executor(
        id = id,
        lat = coords.lat,
        lng = coords.lng,
        heading = coords.heading,
        distance = 0.0
    )
