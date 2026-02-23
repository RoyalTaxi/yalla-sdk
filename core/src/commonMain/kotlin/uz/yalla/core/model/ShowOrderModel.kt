package uz.yalla.core.model

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.kind.PaymentKind
import uz.yalla.core.status.OrderStatus

data class ShowOrderModel(
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
        data class Coords(
            val heading: Double,
            val lat: Double,
            val lng: Double
        )

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

    data class StatusTime(
        val status: String,
        val time: Long
    )

    data class Taxi(
        val bonusAmount: Int,
        val clientTotalPrice: Double,
        val distance: Double,
        val fixedPrice: Boolean,
        val routes: List<Route>,
        val services: List<ServiceModel>,
        val startPrice: Int,
        val tariff: String,
        val tariffId: Int,
        val totalPrice: Int,
        val waitingTime: Int
    ) {
        data class Route(
            val coords: Coords,
            val fullAddress: String,
            val index: Int
        ) {
            data class Coords(
                val lat: Double,
                val lng: Double
            )

            fun toGeoPoint() = GeoPoint(lat = coords.lat, lng = coords.lng)
        }
    }
}

fun ShowOrderModel.Executor.toCommonExecutor() =
    uz.yalla.core.model.Executor(
        id = id,
        lat = coords.lat,
        lng = coords.lng,
        heading = coords.heading,
        distance = 0.0
    )
