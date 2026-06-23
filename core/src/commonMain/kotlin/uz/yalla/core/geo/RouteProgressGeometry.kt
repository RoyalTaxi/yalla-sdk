package uz.yalla.core.geo

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

private const val METERS_PER_DEG_LAT = 111_320.0
private const val DUPLICATE_EPSILON_METERS = 0.05
private const val DEFAULT_LEAD_METERS = 1.0
private const val DEFAULT_BACK_WINDOW_METERS = 5.0
private const val DEFAULT_FORWARD_WINDOW_METERS = 50.0
private const val GLOBAL_FALLBACK_MARGIN_METERS = 1.0

/**
 * Arc-length geometry for a fixed route polyline.
 *
 * The whole moving-car problem is reduced to a single scalar: `progress`, the number of
 * **meters travelled along the route**. Every renderable quantity — the car's position, its
 * heading, and the not-yet-driven remainder of the route — derives from that one scalar via
 * this class, so the car and the drawn route can never disagree (no corner-cutting, no
 * "chasing" the line with straight chords between raw GPS points).
 *
 * The route is immutable, so consecutive duplicate vertices are compacted once in the
 * constructor and the prefix-sum [cumulativeDistances] is precomputed; per-frame calls
 * ([coordinateAt], [headingAt], [remainingRouteFrom]) are then cheap.
 *
 * **Projection is forward-windowed, not memoryless.** Recovering arc-length progress from a raw
 * GPS fix with a *global* nearest-segment scan loses memory of where the car already is, so it
 * un-eats the route on U-turns, loops, and near-parallel grid streets (the globally-closest
 * segment is not the one the car is driving). [projectForward] therefore searches only a bounded
 * window `[lastProgress − back, lastProgress + forward]` around the last known progress and falls
 * back to the global scan ([project]) only on a window miss (a real off-route jump). The back
 * window is a soft band — a legitimate reverse decreases progress inside it rather than freezing —
 * not a hard non-decreasing clamp.
 *
 * Distances use a single planar equirectangular projection (meters; `cos(lat)` longitude scaling),
 * the same metric as [uz.yalla.core.geo] `RouteGeometry`, so a threshold in meters means the same
 * thing here and on the iOS port (pinned by golden-vector tests). This is accurate over the short
 * spans a single route segment covers.
 *
 * Ported from `NavigationRouteProgressGeometry.swift`; the arc-length scalar is reference-faithful,
 * the forward window is the correction the canon panel mandated over the memoryless original.
 *
 * @property totalDistanceMeters total arc length of the compacted route, in meters.
 * @property isValid `true` when the compacted route has at least two distinct points.
 */
public class RouteProgressGeometry(
    route: List<GeoPoint>
) {
    private val route: List<GeoPoint>
    private val cumulativeDistances: List<Double>

    public val totalDistanceMeters: Double

    public val isValid: Boolean
        get() = route.size >= 2

    init {
        val compacted = compactConsecutive(route)
        val cumulative = ArrayList<Double>(compacted.size.coerceAtLeast(1))
        cumulative.add(0.0)
        var total = 0.0
        if (compacted.size > 1) {
            for (index in 1 until compacted.size) {
                total += compacted[index - 1].planarDistanceTo(compacted[index])
                cumulative.add(total)
            }
        }
        this.route = compacted
        this.cumulativeDistances = cumulative
        this.totalDistanceMeters = total
    }

    /** Perpendicular projection of an arbitrary point onto the route. */
    public data class Projection(
        /** Arc-length position (meters from the route start) of the nearest point on the polyline. */
        val progressMeters: Double,
        /** Perpendicular distance (meters) from the projected point to the route at that position. */
        val crossTrackMeters: Double
    )

    /** Clamps [progress] into the valid `0..totalDistanceMeters` range. */
    public fun clamp(progress: Double): Double = progress.coerceIn(0.0, totalDistanceMeters)

    /**
     * Projects [point] onto the route by a **global** nearest-segment scan, returning the
     * arc-length progress of the nearest point on the polyline and the perpendicular cross-track
     * distance to it.
     *
     * Memoryless: it has no notion of where the car already is, so on loops / U-turns / near-parallel
     * streets it can pick a segment the car is not actually on. Use it for the very first fix (no
     * prior progress) and as the bounded-window fallback; per-frame tracking should use
     * [projectForward]. The caller decides on/off-route by comparing [Projection.crossTrackMeters]
     * against a snap threshold.
     */
    public fun project(point: GeoPoint): Projection = projectInRange(point, 0, route.size - 2)

    /**
     * Projects [point] onto the route within a bounded arc-length window around [lastProgressMeters].
     *
     * Searches only the segments overlapping `[lastProgress − backWindowMeters, lastProgress +
     * forwardWindowMeters]`, so a fix near a parallel/return leg cannot snap the car off its current
     * leg, and a noisy backward sample cannot un-eat the whole route. Progress may still decrease
     * **within the back window** (a legitimate reverse / doubleback is not frozen). On a window miss
     * — the point projects nowhere inside the window within reach — it falls back to the global
     * [project] so a real off-route jump is still detected (large [Projection.crossTrackMeters]).
     *
     * @param lastProgressMeters the car's last known arc-length progress, in meters.
     * @param backWindowMeters how far behind [lastProgressMeters] to allow a legitimate reverse.
     * @param forwardWindowMeters how far ahead to search for forward motion (also caps per-frame cost).
     */
    public fun projectForward(
        point: GeoPoint,
        lastProgressMeters: Double,
        backWindowMeters: Double = DEFAULT_BACK_WINDOW_METERS,
        forwardWindowMeters: Double = DEFAULT_FORWARD_WINDOW_METERS
    ): Projection {
        if (route.size < 2) return Projection(0.0, 0.0)
        val anchor = clamp(lastProgressMeters)
        val lowMeters = anchor - backWindowMeters
        val highMeters = anchor + forwardWindowMeters
        val firstSegment = segmentIndexForProgress(lowMeters)
        val lastSegment = segmentIndexForProgress(highMeters)
        val windowed = projectInRange(point, firstSegment, lastSegment)
        // Window miss: the fix sits clearly off every in-window segment, so the car has either jumped
        // forward past a sparse-GPS gap or gone genuinely off-route. Consult the global scan, but only
        // accept it when it lands *ahead* of the window (forward progress) and is clearly closer — a
        // global match *behind* the window is the un-eat / parallel-return-leg trap and is rejected,
        // leaving a large windowed cross-track that the caller reads as off-route.
        if (windowed.crossTrackMeters > backWindowMeters.coerceAtLeast(GLOBAL_FALLBACK_MARGIN_METERS)) {
            val global = project(point)
            val forwardOfWindow = global.progressMeters >= highMeters - GLOBAL_FALLBACK_MARGIN_METERS
            if (forwardOfWindow && global.crossTrackMeters < windowed.crossTrackMeters) return global
        }
        return windowed
    }

    /**
     * Returns the coordinate at arc-length [progress] meters along the route.
     *
     * Binary-searches the prefix-sum table for the containing segment and linearly
     * interpolates within it, so the result is always exactly on the polyline.
     */
    public fun coordinateAt(progress: Double): GeoPoint {
        val first = route.firstOrNull() ?: return GeoPoint.Zero
        if (route.size < 2) return first
        val clamped = clamp(progress)
        if (clamped <= 0.0) return route.first()
        if (clamped >= totalDistanceMeters) return route.last()
        val index = segmentStartIndex(clamped)
        return interpolatedCoordinate(index, clamped)
    }

    /**
     * Returns the route tangent heading (degrees, `0..360`) at arc-length [progress].
     *
     * Computed as the bearing from `coordinateAt(progress - leadMeters)` to
     * `coordinateAt(progress + leadMeters)`, so it follows the polyline direction rather than
     * the chord toward a raw GPS point. At arrival the lead points clamp against the route end, so
     * the heading settles onto the final segment tangent instead of flickering. Returns [fallback]
     * when the route is degenerate or the two lead points coincide.
     */
    public fun headingAt(
        progress: Double,
        fallback: Float,
        leadMeters: Double = DEFAULT_LEAD_METERS
    ): Float {
        if (route.size < 2) return fallback
        val clamped = clamp(progress)
        val from = coordinateAt(clamped - leadMeters)
        val to = coordinateAt(clamped + leadMeters)
        if (from.planarDistanceTo(to) <= 0.0) return fallback
        return from.bearingTo(to).toFloat()
    }

    /**
     * Returns the not-yet-driven remainder of the route, starting at arc-length [progress].
     *
     * The head is the interpolated point at [progress]; the tail is the remaining vertices.
     * As [progress] grows the list shrinks ("eaten", not "chased"). The interpolated head is
     * dropped when it sits on top of the next vertex to avoid a zero-length leading segment.
     */
    public fun remainingRouteFrom(progress: Double): List<GeoPoint> {
        if (route.isEmpty()) return emptyList()
        if (route.size < 2) return listOf(route.first())
        val clamped = clamp(progress)
        if (clamped <= 0.0) return route
        if (clamped >= totalDistanceMeters) return listOf(route.last())
        val index = segmentStartIndex(clamped)
        val head = interpolatedCoordinate(index, clamped)
        val tail = route.subList(index + 1, route.size)
        val next = tail.firstOrNull()
        if (next != null && head.planarDistanceTo(next) <= DUPLICATE_EPSILON_METERS) {
            return tail.toList()
        }
        return buildList(tail.size + 1) {
            add(head)
            addAll(tail)
        }
    }

    /**
     * The single point-to-segment projection primitive: projects [point] onto every segment in the
     * inclusive index range `[fromSegment, toSegment]` and keeps the closest. All projection callers
     * ([project], [projectForward]) route through here, so there is exactly one copy of the planar
     * point-to-segment math in this class.
     */
    private fun projectInRange(
        point: GeoPoint,
        fromSegment: Int,
        toSegment: Int
    ): Projection {
        if (route.size < 2) return Projection(0.0, 0.0)
        val metersPerDegLng = METERS_PER_DEG_LAT * cos(point.lat * PI / 180.0)
        val first = fromSegment.coerceIn(0, route.size - 2)
        val last = toSegment.coerceIn(first, route.size - 2)
        var bestProgress = 0.0
        var bestCrossTrack = Double.MAX_VALUE
        var bestSquaredDistance = Double.MAX_VALUE
        for (index in first..last) {
            val a = route[index]
            val b = route[index + 1]
            val px = (point.lng - a.lng) * metersPerDegLng
            val py = (point.lat - a.lat) * METERS_PER_DEG_LAT
            val bx = (b.lng - a.lng) * metersPerDegLng
            val by = (b.lat - a.lat) * METERS_PER_DEG_LAT
            val segLenSq = bx * bx + by * by
            val t: Double
            val squaredDistance: Double
            val crossTrack: Double
            if (segLenSq == 0.0) {
                t = 0.0
                squaredDistance = px * px + py * py
                crossTrack = sqrt(squaredDistance)
            } else {
                t = ((px * bx + py * by) / segLenSq).coerceIn(0.0, 1.0)
                val dx = px - bx * t
                val dy = py - by * t
                squaredDistance = dx * dx + dy * dy
                crossTrack = sqrt(squaredDistance)
            }
            if (squaredDistance < bestSquaredDistance) {
                bestSquaredDistance = squaredDistance
                val segmentLength = cumulativeDistances[index + 1] - cumulativeDistances[index]
                bestProgress = cumulativeDistances[index] + segmentLength * t
                bestCrossTrack = crossTrack
            }
        }
        return Projection(progressMeters = clamp(bestProgress), crossTrackMeters = bestCrossTrack)
    }

    /** Index of the segment whose arc-length span contains [progress] (clamped to the valid range). */
    private fun segmentIndexForProgress(progress: Double): Int {
        val clamped = clamp(progress)
        if (clamped <= 0.0) return 0
        if (clamped >= totalDistanceMeters) return route.size - 2
        return segmentStartIndex(clamped)
    }

    /**
     * Binary-searches the sorted [cumulativeDistances] for the segment containing [clamped],
     * returning the index of that segment's start vertex.
     *
     * Precondition: `0 < clamped < totalDistanceMeters` (callers handle the endpoints).
     */
    private fun segmentStartIndex(clamped: Double): Int {
        var low = 1
        var high = cumulativeDistances.size - 1
        while (low < high) {
            val mid = (low + high) / 2
            if (cumulativeDistances[mid] >= clamped) high = mid else low = mid + 1
        }
        return low - 1
    }

    private fun interpolatedCoordinate(
        index: Int,
        clamped: Double
    ): GeoPoint {
        val startDistance = cumulativeDistances[index]
        val endDistance = cumulativeDistances[index + 1]
        val segmentLength = endDistance - startDistance
        val t = if (segmentLength > 0.0) (clamped - startDistance) / segmentLength else 0.0
        val a = route[index]
        val b = route[index + 1]
        return GeoPoint(
            lat = a.lat + (b.lat - a.lat) * t,
            lng = a.lng + (b.lng - a.lng) * t
        )
    }

    private companion object {
        private fun compactConsecutive(coordinates: List<GeoPoint>): List<GeoPoint> {
            if (coordinates.isEmpty()) return emptyList()
            val compacted = ArrayList<GeoPoint>(coordinates.size)
            var last: GeoPoint? = null
            for (coordinate in coordinates) {
                val previous = last
                if (previous != null && previous.planarDistanceTo(coordinate) <= DUPLICATE_EPSILON_METERS) {
                    continue
                }
                compacted.add(coordinate)
                last = coordinate
            }
            return compacted
        }

        /**
         * Planar equirectangular distance in meters, scaled by `cos(lat)` for longitude — fast
         * and accurate over the short spans between adjacent route vertices.
         */
        private fun GeoPoint.planarDistanceTo(other: GeoPoint): Double {
            val metersPerDegLng = METERS_PER_DEG_LAT * cos(lat * PI / 180.0)
            val dx = (other.lng - lng) * metersPerDegLng
            val dy = (other.lat - lat) * METERS_PER_DEG_LAT
            return sqrt(dx * dx + dy * dy)
        }
    }
}
