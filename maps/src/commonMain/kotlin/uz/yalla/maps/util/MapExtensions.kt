package uz.yalla.maps.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.LayoutDirection
import io.github.dellisd.spatialk.geojson.BoundingBox
import io.github.dellisd.spatialk.geojson.Position
import uz.yalla.core.geo.GeoPoint
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// ============================================
// Position Conversions
// ============================================

/**
 * Converts this [GeoPoint] to a spatial-k [Position].
 *
 * @return A [Position] with matching latitude and longitude.
 * @since 0.0.1
 */
fun GeoPoint.toPosition(): Position = Position(longitude = lng, latitude = lat)

/**
 * Converts this spatial-k [Position] to a [GeoPoint].
 *
 * @return A [GeoPoint] with matching latitude and longitude.
 * @since 0.0.1
 */
fun Position.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)

/**
 * Converts a (latitude, longitude) pair to a spatial-k [Position].
 *
 * @return A [Position] from the pair values.
 * @since 0.0.1
 */
fun Pair<Double, Double>.toPosition(): Position = Position(latitude = first, longitude = second)

/**
 * Converts a (latitude, longitude) pair to a [GeoPoint].
 *
 * @return A [GeoPoint] from the pair values.
 * @since 0.0.1
 */
fun Pair<Double, Double>.toGeoPoint(): GeoPoint = GeoPoint(first, second)

/**
 * Returns `true` if neither component is zero, indicating a valid coordinate.
 *
 * @return `true` when both first and second are non-zero.
 * @since 0.0.1
 */
fun Pair<Double, Double>.isValid(): Boolean = first != 0.0 && second != 0.0

/**
 * Returns `true` if neither latitude nor longitude is zero, indicating a valid coordinate.
 *
 * @return `true` when both lat and lng are non-zero.
 * @since 0.0.1
 */
fun GeoPoint.isValid(): Boolean = lat != 0.0 && lng != 0.0

// ============================================
// Bounding Box
// ============================================

/**
 * Computes a [BoundingBox] enclosing all points in this list.
 *
 * @return A [BoundingBox] spanning the min/max lat/lng, or an empty box if the list is empty.
 * @since 0.0.1
 */
fun List<GeoPoint>.toBoundingBox(): BoundingBox =
    when {
        isEmpty() -> BoundingBox(emptyList())
        else ->
            BoundingBox(
                west = minOf { it.lng },
                south = minOf { it.lat },
                east = maxOf { it.lng },
                north = maxOf { it.lat }
            )
    }

// ============================================
// Padding Operations
// ============================================

/**
 * Adds two [PaddingValues] component-wise (top+top, bottom+bottom, start+start, end+end).
 *
 * @param other The padding to add.
 * @return A new [PaddingValues] with summed components.
 * @since 0.0.1
 */
infix operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    PaddingValues(
        top = calculateTopPadding() + other.calculateTopPadding(),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
        start = calculateLeftPadding(LayoutDirection.Ltr) + other.calculateLeftPadding(LayoutDirection.Ltr),
        end = calculateRightPadding(LayoutDirection.Ltr) + other.calculateRightPadding(LayoutDirection.Ltr)
    )

/**
 * Compares two [PaddingValues] by their resolved pixel values.
 *
 * @param other The other padding to compare against.
 * @param layoutDirection Layout direction for start/end resolution.
 * @return `true` if all four sides are equal.
 * @since 0.0.1
 */
fun PaddingValues.hasSameValues(
    other: PaddingValues,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr
): Boolean =
    calculateTopPadding() == other.calculateTopPadding() &&
        calculateBottomPadding() == other.calculateBottomPadding() &&
        calculateLeftPadding(layoutDirection) == other.calculateLeftPadding(layoutDirection) &&
        calculateRightPadding(layoutDirection) == other.calculateRightPadding(layoutDirection)

// ============================================
// Geo Calculations
// ============================================

private const val EARTH_RADIUS_KM = 6371.0

/**
 * Calculates the great-circle distance between two [GeoPoint]s using the Haversine formula.
 *
 * @param from Starting coordinate.
 * @param to Destination coordinate.
 * @return Distance in kilometers.
 * @since 0.0.1
 */
fun haversineDistance(
    from: GeoPoint,
    to: GeoPoint
): Double =
    haversineDistance(
        lat1 = from.lat,
        lng1 = from.lng,
        lat2 = to.lat,
        lng2 = to.lng
    )

/**
 * Calculates the great-circle distance between two coordinate pairs using the Haversine formula.
 *
 * @param lat1 Latitude of the first point in degrees.
 * @param lng1 Longitude of the first point in degrees.
 * @param lat2 Latitude of the second point in degrees.
 * @param lng2 Longitude of the second point in degrees.
 * @return Distance in kilometers.
 * @since 0.0.1
 */
fun haversineDistance(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double
): Double {
    val dLat = (lat2 - lat1).toRadians()
    val dLng = (lng2 - lng1).toRadians()
    val a =
        sin(dLat / 2).pow(2) +
            cos(lat1.toRadians()) * cos(lat2.toRadians()) *
            sin(dLng / 2).pow(2)
    return EARTH_RADIUS_KM * 2 * atan2(sqrt(a), sqrt(1 - a))
}

/**
 * Normalizes a heading to the range [0, 360).
 *
 * @param heading Raw heading in degrees (may be negative or >360).
 * @return Heading normalized to [0, 360).
 * @since 0.0.1
 */
fun normalizeHeading(heading: Float): Float {
    val normalized = heading % 360
    return if (normalized < 0) normalized + 360 else normalized
}

/**
 * Computes the target heading that represents the shortest rotational path from [current] to [target].
 *
 * Ensures animations rotate through the minimal arc (never more than 180 degrees).
 *
 * @param current Current heading in degrees.
 * @param target Desired heading in degrees.
 * @return Adjusted target heading that produces the shortest rotation from [current].
 * @since 0.0.1
 */
fun shortestHeadingPath(
    current: Float,
    target: Float,
): Float {
    val normalizedCurrent = normalizeHeading(current)
    val normalizedTarget = normalizeHeading(target)
    var delta = normalizedTarget - normalizedCurrent

    when {
        delta > 180f -> delta -= 360f
        delta < -180f -> delta += 360f
    }

    return current + delta
}

private fun Double.toRadians(): Double = this * (PI / 180.0)
