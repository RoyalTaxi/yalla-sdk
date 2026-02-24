package uz.yalla.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailsRemoteModel(
    val id: Int? = null,
    @SerialName("date_time")
    val dateTime: Double? = null,
    val service: String? = null,
    val status: String? = null,
    @SerialName("status_time")
    val statusTime: List<StatusTime>? = null,
    val executor: Executor? = null,
    val taxi: Taxi? = null,
    val track: List<TaxiTrack>? = null,
    val comment: String? = null,
    @SerialName("payment_type")
    val paymentType: String? = null,
    @SerialName("card_id")
    val cardId: String? = null,
    val number: Long? = null,
    val options: List<Option>? = null,
    val intercity: Intercity? = null,
    val tariff: Tariff? = null,
    val routes: List<Route>? = null
) {
    @Serializable
    data class StatusTime(
        val status: String? = null,
        val time: Double? = null
    )

    @Serializable
    data class Taxi(
        val tariff: String? = null,
        @SerialName("start_price")
        val startPrice: Float? = null,
        val distance: Float? = null,
        @SerialName("client_total_price")
        val clientTotalPrice: Float? = null,
        @SerialName("total_price")
        val totalPrice: Float? = null,
        @SerialName("fixed_price")
        val fixedPrice: Boolean? = null,
        val routes: List<Route>? = null,
        val services: List<Service>? = null,
        @SerialName("use_the_bonus")
        val useTheBonus: Boolean? = null,
        @SerialName("bonus_amount")
        val bonusAmount: Double? = null,
        val award: Award? = null,
        @SerialName("waiting_time")
        val waitingTime: Float? = null,
        @SerialName("waiting_cost")
        val waitingCost: Float? = null,
        val duration: Float? = null
    ) {
        @Serializable
        data class Award(
            @SerialName("payment_award")
            val amount: Int? = null,
            @SerialName("payment_type")
            val type: String? = null
        )
    }

    @Serializable
    data class Executor(
        val id: Int? = null,
        val phone: String? = null,
        @SerialName("given_names")
        val givenNames: String? = null,
        @SerialName("sur_name")
        val surName: String? = null,
        @SerialName("father_name")
        val fatherName: String? = null,
        val photo: String? = null,
        val rating: Int? = null,
        val coords: Coords? = null,
        val driver: Driver? = null
    ) {
        @Serializable
        data class Coords(
            val lat: Double? = null,
            val lng: Double? = null,
            val heading: Double? = null
        )

        @Serializable
        data class Driver(
            val id: Int? = null,
            val color: Color? = null,
            @SerialName("state_number")
            val stateNumber: String? = null,
            val mark: String? = null,
            val model: String? = null,
            val callsign: String? = null
        ) {
            @Serializable
            data class Color(
                val id: Int? = null,
                val color: String? = null,
                @SerialName("name")
                val colorName: String? = null
            )
        }
    }

    @Serializable
    data class Service(
        val id: Int? = null,
        val cost: Double? = null,
        @SerialName("cost_type")
        val costType: String? = null,
        val name: String? = null
    )

    @Serializable
    data class TaxiTrack(
        val accuracy: Double? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        @SerialName("location_type")
        val locationType: String? = null,
        val online: Boolean? = null,
        val speed: Double? = null,
        val status: String? = null,
        val time: Long? = null
    )

    @Serializable
    data class Option(
        val id: Int? = null,
        val cost: Double? = null,
        @SerialName("cost_type")
        val costType: String? = null,
        val name: String? = null
    )

    @Serializable
    data class Intercity(
        @SerialName("start_hour")
        val startHour: String? = null,
        @SerialName("end_hour")
        val endHour: String? = null,
        @SerialName("schedule_id")
        val scheduleId: Int? = null,
        @SerialName("total_price")
        val totalPrice: Double? = null,
        @SerialName("is_booked")
        val isBooked: Boolean? = null,
        @SerialName("seat_layouts")
        val seatLayouts: List<SeatLayout>? = null,
        @SerialName("is_postal")
        val isPostal: Boolean? = null
    ) {
        @Serializable
        data class SeatLayout(
            val slug: String? = null,
            val index: Int? = null,
            @SerialName("seat_layout_id")
            val seatLayoutId: Int? = null,
            val price: Double? = null
        )
    }

    @Serializable
    data class Tariff(
        val id: Int? = null,
        val name: String? = null
    )

    @Serializable
    data class Route(
        val index: Int? = null,
        @SerialName("full_address")
        val fullAddress: String? = null,
        val coords: Coords? = null
    ) {
        @Serializable
        data class Coords(
            val lat: Double? = null,
            val lng: Double? = null
        )
    }
}
