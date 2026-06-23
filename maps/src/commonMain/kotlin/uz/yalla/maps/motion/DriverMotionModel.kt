package uz.yalla.maps.motion

import uz.yalla.core.geo.GeoPoint
import uz.yalla.core.geo.RouteProgressGeometry
import uz.yalla.core.geo.bearingTo
import uz.yalla.core.geo.distanceTo
import uz.yalla.maps.config.RouteFollowingConfig
import uz.yalla.maps.util.interpolateHeading
import uz.yalla.maps.util.shortestHeadingPath

/**
 * Drives a single driver marker between sparse GPS fixes, and owns route-eating as model state.
 *
 * Two modes:
 *
 * - **Chord mode** (the production default): straight-line interpolation between the last and the
 *   new GPS point with snap/teleport heuristics. This is the original behavior and is unchanged.
 *   It is the mode whenever [routeFollowingEnabled] is `false` (the default) or no valid route is set.
 *
 * - **Route mode** (gated behind [routeFollowingEnabled], opt-in via [setRoute]): the car is reduced
 *   to a single scalar — `progress`, meters travelled along the route. Position is always
 *   [RouteProgressGeometry.coordinateAt], heading is the polyline tangent eased with a per-second
 *   turn-rate cap, and the remaining route ([remainingRoute]) is trimmed behind the car *here, in the
 *   model* — renderers only draw what they are handed (Humble Object). Incoming GPS is projected onto
 *   the route through a bounded forward window (no un-eating on loops / U-turns / parallel streets).
 *
 * **Off-route honesty.** Route state is binary ([RouteState.ON_ROUTE] / [RouteState.OFF_ROUTE]) with
 * enter/exit hysteresis from [routeConfig]. The client owns off-route detection + refetch
 * (OrderSheetViewModel.OffRouteTracker, ADR 0002); the SDK only uses the state to render. While
 * on-route but offset, [connector] exposes the raw-GPS → snapped line so the snap is never a silent
 * lie. Off-route falls back to chord interpolation so the car never freezes.
 *
 * The route-following path is feature-flagged OFF by default; chord interpolation stays the
 * production default until a platform flips the flag on-device (see ADR 0003).
 *
 * @param minMoveMeters chord mode: minimum movement to count as a real move (else rotate in place).
 * @param teleportSpeedMps chord mode: implied speed above which a fix snaps instead of animating.
 * @param minDurationMs lower clamp on the animation interval between fixes.
 * @param maxDurationMs upper clamp on the animation interval between fixes.
 * @param defaultDurationMs interval used for the very first fix.
 * @param routeFollowingEnabled feature flag; when `false` (default) [setRoute] is a no-op and the
 *   model behaves as the original chord-only model.
 * @param routeConfig named thresholds (snap / arrival / connector), hysteresis, cooldown, and the
 *   projection window for route mode.
 */
public class DriverMotionModel(
    private val minMoveMeters: Double = 1.5,
    private val teleportSpeedMps: Double = 50.0,
    private val minDurationMs: Long = 1_000L,
    private val maxDurationMs: Long = 12_000L,
    private val defaultDurationMs: Long = 10_000L,
    private val routeFollowingEnabled: Boolean = false,
    private val routeConfig: RouteFollowingConfig = RouteFollowingConfig()
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

    // Route-following state. Inactive whenever [geometry] is null/invalid, the flag is off, or the
    // latest fix was off-route (then chord state above takes over).
    private var geometry: RouteProgressGeometry? = null
    private var routeMode: Boolean = false
    private var routeState: RouteState = RouteState.ON_ROUTE
    private var startProgress: Double = 0.0
    private var targetProgress: Double = 0.0
    private var displayProgress: Double = 0.0
    private var lastSampleMs: Long = 0L

    // Connector (raw GPS -> snapped) for the last on-route fix; null when the fix sat on the line.
    private var rawFix: GeoPoint? = null
    private var connectorVisible: Boolean = false

    /**
     * Sets (or clears) the route this model follows.
     *
     * A no-op unless [routeFollowingEnabled] is `true`. A non-null, valid route then switches the
     * model into route mode and re-projects the current displayed point to seed [displayProgress].
     * A null or invalid route clears route mode; the model then behaves exactly like the original
     * chord-only [DriverMotionModel].
     */
    public fun setRoute(route: List<GeoPoint>?) {
        if (!routeFollowingEnabled) return
        val newGeometry = route?.let { RouteProgressGeometry(it) }?.takeIf { it.isValid }
        geometry = newGeometry
        if (newGeometry == null) {
            routeMode = false
            routeState = RouteState.ON_ROUTE
            rawFix = null
            connectorVisible = false
            return
        }
        val seed = targetPoint?.let { sample(lastSampleMs).point } ?: targetPoint
        val seededProgress = seed?.let { newGeometry.project(it).progressMeters } ?: 0.0
        startProgress = seededProgress
        targetProgress = seededProgress
        displayProgress = seededProgress
    }

    public fun push(
        point: GeoPoint,
        routeHint: Float?,
        serverHeading: Float?,
        atMillis: Long
    ) {
        val activeGeometry = geometry
        if (activeGeometry != null && activeGeometry.isValid) {
            val seeded = targetPoint != null || routeMode
            val projection =
                if (seeded) {
                    activeGeometry.projectForward(
                        point = point,
                        lastProgressMeters = displayProgress,
                        backWindowMeters = routeConfig.backWindowMeters,
                        forwardWindowMeters = routeConfig.forwardWindowMeters
                    )
                } else {
                    activeGeometry.project(point)
                }
            if (isOnRouteAfterHysteresis(projection.crossTrackMeters)) {
                routeState = RouteState.ON_ROUTE
                pushRoute(activeGeometry, projection, point, atMillis)
                return
            }
            // Off-route: fall through to chord. The client owns off-route detection + refetch
            // (OrderSheetViewModel.OffRouteTracker); the SDK only renders.
            routeState = RouteState.OFF_ROUTE
            routeMode = false
            rawFix = null
            connectorVisible = false
        }
        pushChord(point, routeHint, serverHeading, atMillis)
    }

    private fun pushRoute(
        activeGeometry: RouteProgressGeometry,
        projection: RouteProgressGeometry.Projection,
        point: GeoPoint,
        atMillis: Long
    ) {
        if (!routeMode || targetPoint == null) {
            if (targetPoint == null) displayProgress = projection.progressMeters
            routeMode = true
            startProgress = displayProgress
        } else {
            startProgress = sampleRouteProgress(atMillis)
        }
        // The forward window already prevents un-eating; clamp the target up so a tiny within-window
        // reverse on a *forward-driving* fix does not stutter the animation backward.
        targetProgress = maxOf(startProgress, projection.progressMeters)
        val elapsed = (atMillis - lastFixMs).coerceAtLeast(1L)
        durationMs = elapsed.coerceIn(minDurationMs, maxDurationMs)
        startMs = atMillis
        lastFixMs = atMillis
        startPoint = activeGeometry.coordinateAt(startProgress)
        targetPoint = activeGeometry.coordinateAt(targetProgress)
        snap = false

        rawFix = point
        val snapped = activeGeometry.coordinateAt(projection.progressMeters)
        connectorVisible = point.distanceTo(snapped) > routeConfig.connectorHideThreshold
    }

    private fun pushChord(
        point: GeoPoint,
        routeHint: Float?,
        serverHeading: Float?,
        atMillis: Long
    ) {
        routeMode = false
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

    public fun hasFix(): Boolean = targetPoint != null

    /** `true` when the most recent GPS fix was beyond the off-route threshold. */
    public fun isOffRoute(): Boolean = routeState == RouteState.OFF_ROUTE

    /** The binary off-route state surfaced for the client (renderer + refetch owner). */
    public fun routeState(): RouteState = routeState

    /** `true` while the model is actively following a route. */
    public fun isFollowingRoute(): Boolean = routeMode

    /**
     * The honesty connector from the raw GPS fix to the drawn (snapped) car, or `null` when there is
     * no route mode, no fix, or the raw fix sits on the line. The renderer draws this line verbatim.
     */
    public fun connector(atMillis: Long): RouteConnector? {
        if (!routeMode || !connectorVisible) return null
        val raw = rawFix ?: return null
        return RouteConnector(rawPoint = raw, snappedPoint = sample(atMillis).point)
    }

    public fun sample(atMillis: Long): Pose {
        if (routeMode) {
            val activeGeometry = geometry
            if (activeGeometry != null) return sampleRoute(activeGeometry, atMillis)
        }
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

    private fun sampleRoute(
        activeGeometry: RouteProgressGeometry,
        atMillis: Long
    ): Pose {
        val progress = sampleRouteProgress(atMillis)
        displayProgress = progress
        val point = activeGeometry.coordinateAt(progress)
        val targetHeading = activeGeometry.headingAt(progress, fallback = displayBearing)
        val dtSeconds =
            if (lastSampleMs == 0L) 0.0 else (atMillis - lastSampleMs).coerceAtLeast(0L) / 1000.0
        displayBearing = easeHeading(displayBearing, targetHeading, dtSeconds)
        lastSampleMs = atMillis
        return Pose(point, displayBearing)
    }

    private fun sampleRouteProgress(atMillis: Long): Double {
        val fraction =
            if (durationMs > 0L) {
                ((atMillis - startMs).toDouble() / durationMs).coerceIn(0.0, 1.0)
            } else {
                1.0
            }
        return startProgress + (targetProgress - startProgress) * fraction
    }

    /**
     * Applies enter/exit hysteresis to a raw cross-track distance: a fix in the band between
     * [RouteFollowingConfig.offRouteExitMeters] and [RouteFollowingConfig.offRouteEnterMeters] keeps
     * the current state, so the binary state does not flicker at the boundary.
     */
    private fun isOnRouteAfterHysteresis(crossTrackMeters: Double): Boolean =
        when (routeState) {
            RouteState.ON_ROUTE -> crossTrackMeters <= routeConfig.offRouteEnterMeters
            RouteState.OFF_ROUTE -> crossTrackMeters <= routeConfig.offRouteExitMeters
        }

    /**
     * Eases [current] heading toward [target] with a per-second turn-rate cap so a sharp corner
     * is rotated through smoothly rather than snapped. With no elapsed time the heading is held.
     */
    private fun easeHeading(
        current: Float,
        target: Float,
        dtSeconds: Double
    ): Float {
        if (dtSeconds <= 0.0) return current
        val unwrappedTarget = shortestHeadingPath(current, target)
        val delta = unwrappedTarget - current
        val maxDelta = (routeConfig.maxHeadingTurnRatePerSecond * dtSeconds).toFloat()
        val clamped = delta.coerceIn(-maxDelta, maxDelta)
        return current + clamped
    }

    /**
     * Remaining (not-yet-driven) route at the current displayed progress — computed here in the
     * model and handed to the renderer to draw verbatim (Humble Object; no geo math in renderers).
     *
     * Empty when no route is set or the car has arrived. Calling this samples progress at [atMillis]
     * so the returned list shrinks in step with the animated car.
     */
    public fun remainingRoute(atMillis: Long): List<GeoPoint> {
        val activeGeometry = geometry ?: return emptyList()
        if (!routeMode) return emptyList()
        val progress = sampleRouteProgress(atMillis)
        if (activeGeometry.totalDistanceMeters - progress <= routeConfig.routeArrivalThreshold) {
            return emptyList()
        }
        return activeGeometry.remainingRouteFrom(progress)
    }

    private fun resolveHint(
        routeHint: Float?,
        serverHeading: Float?
    ): Float? = routeHint ?: serverHeading?.takeIf { it != 0f }

    public companion object {
        public fun withDefaults(): DriverMotionModel = DriverMotionModel()
    }
}
