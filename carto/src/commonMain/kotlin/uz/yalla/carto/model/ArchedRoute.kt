package uz.yalla.carto.model

import uz.yalla.core.geo.GeoPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

public enum class ArchSide {
    LEFT,
    RIGHT
}

public fun archedPoints(
    from: GeoPoint,
    to: GeoPoint,
    curvature: Double = 0.2,
    side: ArchSide = ArchSide.LEFT,
    samples: Int = 48
): List<GeoPoint> {
    val count = maxOf(samples, 2)

    val midLat = (from.lat + to.lat) / 2.0
    val metersPerDegLat = 111_320.0
    val metersPerDegLon = 111_320.0 * cos(midLat * PI / 180.0)

    val bx = (to.lng - from.lng) * metersPerDegLon
    val by = (to.lat - from.lat) * metersPerDegLat

    val length = sqrt(bx * bx + by * by)
    if (length <= 0.0) return listOf(from, to)

    val signed = if (side == ArchSide.LEFT) 1.0 else -1.0
    val nx = -by / length * signed
    val ny = bx / length * signed

    val offset = curvature * length
    val cx = bx / 2.0 + nx * offset
    val cy = by / 2.0 + ny * offset

    return List(count) { i ->
        val t = i.toDouble() / (count - 1)
        val omt = 1.0 - t
        val x = 2 * omt * t * cx + t * t * bx
        val y = 2 * omt * t * cy + t * t * by
        GeoPoint(
            lat = from.lat + y / metersPerDegLat,
            lng = from.lng + x / metersPerDegLon
        )
    }
}
