package uz.yalla.core.geo


data class GeoPoint(
    val lat: Double,
    val lng: Double
) {
    companion object {
        val Zero = GeoPoint(0.0, 0.0)
    }
}
