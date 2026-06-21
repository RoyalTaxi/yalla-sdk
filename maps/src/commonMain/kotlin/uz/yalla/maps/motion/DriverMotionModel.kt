package uz.yalla.maps.motion

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.geo.bearingTo
import uz.yalla.core.geo.distanceTo
import uz.yalla.maps.util.interpolateHeading

/**
 * Pure, value-testable motion core for an animated driver marker: it owns position interpolation,
 * teleport detection, and heading precedence so every platform shares one behaviour ("share the
 * math, not the shell"). Fed positional fixes via [push] (typically ~1/sec from the network) and
 * read every frame via [sample] (driven by a thin platform shell — `CADisplayLink`/`Choreographer`).
 *
 * Threading: this holds plain mutable state and is **not** synchronized. [push] and [sample] must be
 * called from the same single thread (the frame thread); feeding [push] from an off-thread location
 * callback while [sample] runs on the render loop is a torn read and is not supported.
 *
 * @param minMoveMeters minimum movement before a derived bearing is trusted over hints.
 * @param teleportSpeedMps implied speed (from raw elapsed time) above which a fix snaps instead of animating.
 * @param minDurationMs lower clamp for the animation tween window.
 * @param maxDurationMs upper clamp for the animation tween window.
 * @param defaultDurationMs tween window used for the very first fix.
 */
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

    /**
     * Feeds one positional fix into the model. Heading precedence, highest trust first:
     * 1. bearing derived from consecutive fixes when the car moved >= [minMoveMeters];
     * 2. otherwise the last good derived bearing is held;
     * 3. otherwise [routeHint] — a route-segment bearing the caller resolved (null when off-route);
     * 4. otherwise [serverHeading] when non-zero (0f is treated as absent: the server collapses a
     *    null heading to 0.0/north upstream, so a real northbound hint is indistinguishable).
     *
     * The model — not the caller — owns this ordering: pass route-snap as [routeHint] and the raw
     * server heading as [serverHeading]; never pre-flatten them into one value.
     */
    public fun push(
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
            }
            startBearing = targetBearing
            snap = true
            return
        }

        val displayedPoint = sample(atMillis).point
        // Two distinct concerns must not share one clamped value: the teleport test needs the
        // *real* elapsed time between fixes, while the animation duration is the same gap clamped
        // to a sane tween window. Clamping the elapsed time before computing speed corrupted the
        // teleport decision in both directions (sparse-GPS moves >12s apart falsely snapped, and
        // genuine sub-second jumps failed to snap).
        val elapsed = (atMillis - lastFixMs).coerceAtLeast(1L)
        val interval = elapsed.coerceIn(minDurationMs, maxDurationMs)
        val movedMeters = previousTarget.distanceTo(point)
        val impliedSpeed = movedMeters / (elapsed / 1000.0)

        if (movedMeters >= minMoveMeters) {
            targetBearing = previousTarget.bearingTo(point).toFloat()
            hasGoodBearing = true
        } else if (!hasGoodBearing) {
            resolveHint(routeHint, serverHeading)?.let { targetBearing = it }
        }

        snap = impliedSpeed > teleportSpeedMps
        startPoint = if (snap) point else displayedPoint
        startBearing = if (snap) targetBearing else displayBearing
        targetPoint = point
        startMs = atMillis
        durationMs = interval
        lastFixMs = atMillis
    }

    /**
     * True once at least one [push] has been received. Before the first fix, [sample] returns a
     * sentinel [Pose] at [GeoPoint.Zero] (null-island) — the same value the rest of the module
     * treats as "absent" — so a frame driver MUST gate rendering on this and skip until a fix
     * exists, otherwise the marker lands at (0,0) on startup before jumping to the real position.
     */
    public fun hasFix(): Boolean = targetPoint != null

    /**
     * Samples the interpolated [Pose] for [atMillis]. Returns a sentinel pose at [GeoPoint.Zero]
     * until the first [push]; callers must check [hasFix] and not render until it is true.
     */
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

    public companion object {
        /** Creates a model with the default tuning. Equivalent to `DriverMotionModel()`. */
        public fun withDefaults(): DriverMotionModel = DriverMotionModel()
    }
}
