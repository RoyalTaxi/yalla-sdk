package uz.yalla.core.geo

/** A WGS-84 geographic coordinate: latitude [lat] and longitude [lng] in decimal degrees. */
public data class GeoPoint(
    val lat: Double,
    val lng: Double
) {
    public companion object {
        /** The null island, `(0.0, 0.0)` — a neutral default, not a valid user location. */
        public val Zero: GeoPoint = GeoPoint(0.0, 0.0)
    }
}
