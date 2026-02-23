package uz.yalla.core.model.location

data class PlaceNameModel(
    val db: Boolean,
    val displayName: String,
    val id: Int?,
    val lat: Double,
    val lng: Double
)
