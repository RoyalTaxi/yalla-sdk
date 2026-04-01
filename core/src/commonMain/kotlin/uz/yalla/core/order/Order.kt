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
    /**
     * Assigned driver with contact info, real-time location, and vehicle details.
     *
     * This is the full executor model embedded within an [Order]. For lightweight
     * map tracking, convert to [uz.yalla.core.order.Executor] via [toExecutor].
     *
     * @property coords Real-time driver coordinates and heading
     * @property vehicle Driver's vehicle information
     * @property fatherName Driver's patronymic name
     * @property givenNames Driver's given/first names
     * @property id Unique driver identifier
     * @property phone Driver's contact phone number
     * @property photo URL to the driver's profile photo
     * @property rating Driver's average rating (0.0-5.0)
     * @property surName Driver's surname/family name
     * @see toExecutor
     * @since 0.0.1
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
         * @property lat Current latitude
         * @property lng Current longitude
         * @since 0.0.1
         */
        data class Coords(
            val heading: Double,
            val lat: Double,
            val lng: Double
        )

        /**
         * Driver's vehicle information for display in the order card.
         *
         * @property callsign Dispatch callsign/identifier
         * @property color Vehicle color details
         * @property id Unique vehicle identifier
         * @property mark Vehicle manufacturer/make (e.g., "Chevrolet")
         * @property model Vehicle model name (e.g., "Cobalt")
         * @property stateNumber License plate number
         * @since 0.0.1
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
             * @since 0.0.1
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
     * @property time Epoch timestamp (seconds or milliseconds) of the transition
     * @see OrderStatus
     * @since 0.0.1
     */
    data class StatusTime(
        val status: String,
        val time: Long
    )

    /**
     * Route, pricing, and tariff details for the taxi order.
     *
     * @property bonusAmount Bonus amount applied to this order
     * @property clientTotalPrice Final price visible to the client (after discounts)
     * @property distance Total route distance in meters
     * @property fixedPrice Whether the price was fixed at order creation (not metered)
     * @property routes Ordered list of waypoints (pickup, intermediate stops, destination)
     * @property services Extra services added to this order (e.g., child seat, luggage)
     * @property startPrice Base/starting fare in smallest currency unit
     * @property tariff Display name of the selected tariff
     * @property tariffId Unique tariff identifier
     * @property totalPrice Total fare before bonus/discount in smallest currency unit
     * @property waitingTime Accumulated waiting time in seconds
     * @see ExtraService
     * @since 0.0.1
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
         * @property coords Geographic coordinates of the waypoint
         * @property fullAddress Human-readable address string
         * @property index Zero-based position in the route (0 = pickup, last = destination)
         * @since 0.0.1
         */
        data class Route(
            val coords: Coords,
            val fullAddress: String,
            val index: Int
        ) {
            /**
             * Geographic coordinates of a route waypoint.
             *
             * @property lat Latitude
             * @property lng Longitude
             * @since 0.0.1
             */
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
 * @since 0.0.1
 */
fun Order.Executor.toExecutor() =
    Executor(
        id = id,
        lat = coords.lat,
        lng = coords.lng,
        heading = coords.heading,
        distance = 0.0
    )
