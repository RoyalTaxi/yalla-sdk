package uz.yalla.network

import uz.yalla.core.geo.GeoPoint
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

private const val POSITION_DECIMALS = 6
private const val POSITION_SCALE = 1_000_000L

internal fun formatPosition(point: GeoPoint): String = "${formatCoordinate(point.lat)} ${formatCoordinate(point.lng)}"

private fun formatCoordinate(value: Double): String {
    val scaled = (value.absoluteValue * POSITION_SCALE).roundToLong()
    val whole = scaled / POSITION_SCALE
    val fraction = scaled % POSITION_SCALE
    val sign = if (value < 0 && scaled != 0L) "-" else ""
    val fractionDigits = fraction.toString().padStart(POSITION_DECIMALS, '0')
    return "$sign$whole.$fractionDigits"
}
