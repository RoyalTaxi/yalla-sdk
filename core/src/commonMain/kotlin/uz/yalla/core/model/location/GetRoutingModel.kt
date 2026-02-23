package uz.yalla.core.model.location

data class GetRoutingModel(
    val distance: Double,
    val duration: Double,
    val routing: List<Routing>
) {
    data class Routing(
        val lat: Double,
        val lng: Double
    )
}
