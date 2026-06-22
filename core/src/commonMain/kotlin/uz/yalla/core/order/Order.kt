package uz.yalla.core.order

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.identity.DriverId
import uz.yalla.core.identity.OrderId
import uz.yalla.core.payment.PaymentMethod

public data class Order(
    val comment: String,
    val dateTime: Long,
    val driver: Driver,
    val id: OrderId,
    val paymentMethod: PaymentMethod,
    val service: String,
    val status: OrderStatus,
    val statusTime: List<StatusTime>,
    val taxi: Taxi
) {
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
}
