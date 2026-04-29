package uz.yalla.core.order

import uz.yalla.core.payment.PaymentKind

/**
 * Complete taxi order aggregate containing all order details.
 *
 * This is the primary domain model returned from order-related API calls.
 * Contains nested types for executor info, vehicle details, route, and pricing.
 *
 * @property dateTime Order creation timestamp (epoch millis)
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
    /**
     * Assigned driver with contact info, real-time location, and vehicle details.
     *
     * This is the full executor model embedded within an [Order]. For lightweight
     * map tracking, convert to [uz.yalla.core.order.Executor] via [toExecutor].
     *
     * @property fatherName Driver's patronymic name
     * @property rating Driver's average rating (0.0-5.0)
     * @see toExecutor
     */
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
        /**
         * Real-time driver coordinates and heading.
         *
         * @property heading Vehicle heading in degrees (0-360, clockwise from north)
         */
        data class Coords(
            val heading: Double,
            val lat: Double,
            val lng: Double
        )

        /**
         * Driver's vehicle information for display in the order card.
         *
         * @property mark Vehicle manufacturer/make (e.g., "Chevrolet")
         * @property model Vehicle model name (e.g., "Cobalt")
         * @property stateNumber License plate number
         */
        data class Vehicle(
            val callsign: String,
            val color: Color,
            val id: Int,
            val mark: String,
            val model: String,
            val stateNumber: String
        ) {
            /**
             * Vehicle color with both hex value and localized display name.
             *
             * @property color Hex color string (e.g., "#FFFFFF")
             * @property name Localized color name for display (e.g., "White")
             */
            data class Color(
                val color: String,
                val name: String
            )
        }
    }

    /**
     * Records when the order transitioned to a specific status.
     *
     * @property status Status identifier string matching [OrderStatus.id]
     * @property time Epoch timestamp **in seconds** as returned by the backend.
     *   Use [uz.yalla.core.util.toLocalFormattedDate] / [uz.yalla.core.util.toLocalFormattedTime]
     *   for display — those handle the second/millisecond detection.
     * @see OrderStatus
     */
    data class StatusTime(
        val status: String,
        val time: Long
    )

    /**
     * Route, pricing, and tariff details for the taxi order.
     *
     * @property bonusAmount Bonus points applied as a discount, in smallest currency
     *   unit. Already deducted from [totalPrice] to produce [clientTotalPrice].
     * @property clientTotalPrice Final price visible to the client (after discounts)
     * @property distance Total route distance in meters
     * @property fixedPrice Whether the price was fixed at order creation (not metered)
     * @property routes Ordered list of waypoints (pickup, intermediate stops, destination)
     * @property services Extra services added to this order (e.g., child seat, luggage)
     * @property startPrice Base/starting fare in smallest currency unit
     * @property tariff Human-readable tariff name for display ("Economy", "Comfort"…)
     * @property tariffId Server-side tariff identifier; use this when re-ordering
     *   or filtering, not the display string.
     * @property totalPrice Total fare before bonus/discount in smallest currency unit
     * @property waitingTime Accumulated waiting time in seconds
     * @see ExtraService
     */
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
        /**
         * A waypoint in the order's route.
         *
         * @property index Zero-based position in the route (0 = pickup, last = destination)
         */
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

/**
 * Converts this detailed [Order.Executor] to a lightweight [Executor] for map tracking.
 *
 * Extracts only the position data needed for rendering the driver marker.
 * The [Executor.distance] is set to `0.0` and should be computed separately if needed.
 *
 * @return A lightweight [Executor] with position and heading data
 * @see Executor
 */
fun Order.Executor.toExecutor() =
    Executor(
        id = id,
        lat = coords.lat,
        lng = coords.lng,
        heading = coords.heading,
        distance = 0.0
    )
