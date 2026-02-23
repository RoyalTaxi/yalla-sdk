package uz.yalla.core.model.location

data class PolygonRemoteItem(
    val addressId: Int,
    val polygons: List<Polygon>
) {
    data class Polygon(
        val lat: Double,
        val lng: Double
    )
}
