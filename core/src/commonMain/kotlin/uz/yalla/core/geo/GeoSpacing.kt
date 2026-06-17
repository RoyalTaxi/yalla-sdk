package uz.yalla.core.geo

public inline fun <T> List<T>.spacedApartBy(
    minMeters: Double,
    point: (T) -> GeoPoint
): List<T> {
    val kept = ArrayList<T>(size)
    for (item in this) {
        val candidate = point(item)
        if (kept.none { point(it).distanceTo(candidate) < minMeters }) kept.add(item)
    }
    return kept
}
