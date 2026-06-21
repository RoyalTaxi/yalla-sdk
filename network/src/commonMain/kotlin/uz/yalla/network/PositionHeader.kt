package uz.yalla.network

import uz.yalla.core.geo.GeoPoint
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

/**
 * Number of fractional digits used for the `x-position` header. Six decimals is ~0.11 m at the
 * equator — finer than any consumer-GPS fix — and bounds the wire string so the format is stable.
 */
private const val POSITION_DECIMALS = 6
private const val POSITION_SCALE = 1_000_000L // 10^POSITION_DECIMALS

/**
 * Formats [point] as the `"lat lng"` value of the `x-position` header.
 *
 * Uses an explicit fixed-precision conversion rather than [Double.toString]: K/N and the JVM do not
 * guarantee an identical shortest-round-trip rendering of a `Double`, so relying on the platform's
 * `toString` would let the same coordinate ship a different wire string on Android vs iOS. Rounding
 * both axes to [POSITION_DECIMALS] decimals through a shared integer path makes the emitted string
 * byte-identical across platforms.
 */
internal fun formatPosition(point: GeoPoint): String = "${formatCoordinate(point.lat)} ${formatCoordinate(point.lng)}"

private fun formatCoordinate(value: Double): String {
    val scaled = (value.absoluteValue * POSITION_SCALE).roundToLong()
    val whole = scaled / POSITION_SCALE
    val fraction = scaled % POSITION_SCALE
    val sign = if (value < 0 && scaled != 0L) "-" else ""
    val fractionDigits = fraction.toString().padStart(POSITION_DECIMALS, '0')
    return "$sign$whole.$fractionDigits"
}
