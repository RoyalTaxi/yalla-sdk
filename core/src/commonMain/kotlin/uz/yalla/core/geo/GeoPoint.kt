package uz.yalla.core.geo


public data class GeoPoint(
    val lat: Double,
    val lng: Double
) {
    public companion object {
        public val Zero: GeoPoint = GeoPoint(0.0, 0.0)
    }
}
