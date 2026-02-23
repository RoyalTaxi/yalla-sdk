package uz.yalla.core.model.location

data class GetRoutingRequestItemDto(
    val type: String,
    val lng: Double,
    val lat: Double
)
