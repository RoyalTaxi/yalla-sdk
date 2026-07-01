package uz.yalla.carto.motion

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.geo.bearingTo
import uz.yalla.core.geo.distanceTo

public data class Pose(
    val point: GeoPoint,
    val bearing: Float
)

internal fun normalizeHeading(headingDegrees: Float): Float {
    var normalized = headingDegrees % 360f
    if (normalized < 0f) normalized += 360f
    return normalized
}

internal fun shortestHeadingPath(
    fromDegrees: Float,
    toDegrees: Float
): Float {
    var delta = normalizeHeading(toDegrees) - normalizeHeading(fromDegrees)
    if (delta > 180f) delta -= 360f
    if (delta < -180f) delta += 360f
    return fromDegrees + delta
}

internal fun interpolateHeading(
    fromDegrees: Float,
    toDegrees: Float,
    fraction: Float
): Float {
    val targetUnwrapped = shortestHeadingPath(fromDegrees, toDegrees)
    return fromDegrees + (targetUnwrapped - fromDegrees) * fraction.coerceIn(0f, 1f)
}

public class DriverMotionModel(
    private val minMoveMeters: Double = 1.5,
    private val teleportSpeedMps: Double = 50.0,
    private val minDurationMs: Long = 1_000L,
    private val maxDurationMs: Long = 12_000L,
    private val defaultDurationMs: Long = 10_000L
) {
    private var startPoint: GeoPoint? = null
    private var targetPoint: GeoPoint? = null
    private var startMs: Long = 0L
    private var durationMs: Long = defaultDurationMs
    private var lastFixMs: Long = 0L
    private var startBearing: Float = 0f
    private var targetBearing: Float = 0f
    private var displayBearing: Float = 0f
    private var hasGoodBearing: Boolean = false
    private var snap: Boolean = true

    public fun push(
        point: GeoPoint,
        routeHint: Float?,
        serverHeading: Float?,
        atMillis: Long
    ) {
        pushChord(point, routeHint, serverHeading, atMillis)
    }

    private fun pushChord(
        point: GeoPoint,
        routeHint: Float?,
        serverHeading: Float?,
        atMillis: Long
    ) {
        val previousTarget = targetPoint
        if (previousTarget == null) {
            startPoint = point
            targetPoint = point
            startMs = atMillis
            lastFixMs = atMillis
            durationMs = defaultDurationMs
            resolveHint(routeHint, serverHeading)?.let {
                targetBearing = it
                displayBearing = it
                hasGoodBearing = true
            }
            startBearing = targetBearing
            snap = true
            return
        }

        val displayedPoint = sample(atMillis).point
        val elapsed = (atMillis - lastFixMs).coerceAtLeast(1L)
        val interval = elapsed.coerceIn(minDurationMs, maxDurationMs)
        val movedMeters = previousTarget.distanceTo(point)
        val impliedSpeed = movedMeters / (elapsed / 1000.0)
        val shouldMove = movedMeters >= minMoveMeters

        if (routeHint != null) {
            targetBearing = routeHint
            hasGoodBearing = true
        } else if (shouldMove) {
            targetBearing = previousTarget.bearingTo(point).toFloat()
            hasGoodBearing = true
        } else if (!hasGoodBearing) {
            serverHeading?.takeIf { it != 0f }?.let { targetBearing = it }
        }

        if (!shouldMove) {
            snap = false
            startPoint = displayedPoint
            startBearing = displayBearing
            targetPoint = displayedPoint
            startMs = atMillis
            durationMs = interval
            lastFixMs = atMillis
            return
        }

        snap = impliedSpeed > teleportSpeedMps
        startPoint = if (snap) point else displayedPoint
        startBearing = if (snap) targetBearing else displayBearing
        targetPoint = point
        startMs = atMillis
        durationMs = interval
        lastFixMs = atMillis
    }

    public fun sample(atMillis: Long): Pose {
        val target = targetPoint ?: return Pose(GeoPoint.Zero, displayBearing)
        if (snap) {
            displayBearing = targetBearing
            return Pose(target, targetBearing)
        }
        val start = startPoint ?: target
        val fraction =
            if (durationMs > 0L) {
                ((atMillis - startMs).toFloat() / durationMs).coerceIn(0f, 1f)
            } else {
                1f
            }
        val point =
            GeoPoint(
                lat = start.lat + (target.lat - start.lat) * fraction,
                lng = start.lng + (target.lng - start.lng) * fraction
            )
        val bearing = interpolateHeading(startBearing, targetBearing, fraction)
        displayBearing = bearing
        return Pose(point, bearing)
    }

    private fun resolveHint(
        routeHint: Float?,
        serverHeading: Float?
    ): Float? = routeHint ?: serverHeading?.takeIf { it != 0f }
}
