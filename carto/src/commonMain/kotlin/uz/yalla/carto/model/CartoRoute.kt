package uz.yalla.carto.model

import uz.yalla.core.geo.GeoPoint

public data class CartoRoute(
    val id: String,
    val points: List<GeoPoint>,
    val colorArgb: Int,
    val widthDp: Float = 6f,
    val pattern: LinePattern = LinePattern.SOLID,
    val zBand: ZBand = ZBand.ROUTES
)
