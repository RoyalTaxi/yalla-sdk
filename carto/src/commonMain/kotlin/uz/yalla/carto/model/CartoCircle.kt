package uz.yalla.carto.model

import uz.yalla.core.geo.GeoPoint

public data class CartoCircle(
    val id: String,
    val center: GeoPoint,
    val radiusMeters: Double,
    val fillArgb: Int,
    val strokeArgb: Int,
    val strokeWidthDp: Float = 1f,
    val zBand: ZBand = ZBand.CIRCLES
)
