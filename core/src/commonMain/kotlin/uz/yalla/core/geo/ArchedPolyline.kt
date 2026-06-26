package uz.yalla.core.geo

public fun archedPolyline(
    from: GeoPoint,
    to: GeoPoint,
    curvature: Double = 0.5,
    segments: Int = 64,
): List<GeoPoint> {
    if (from == to || segments < 1) return listOf(from, to)
    val midLat = (from.lat + to.lat) / 2.0
    val midLng = (from.lng + to.lng) / 2.0
    val deltaLat = to.lat - from.lat
    val deltaLng = to.lng - from.lng
    val offset = curvature * 0.5
    val controlLat = midLat + deltaLng * offset
    val controlLng = midLng - deltaLat * offset
    return (0..segments).map { step ->
        val t = step.toDouble() / segments
        val inverse = 1.0 - t
        GeoPoint(
            lat = inverse * inverse * from.lat + 2.0 * inverse * t * controlLat + t * t * to.lat,
            lng = inverse * inverse * from.lng + 2.0 * inverse * t * controlLng + t * t * to.lng,
        )
    }
}
