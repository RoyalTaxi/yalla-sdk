package uz.yalla.maps.motion

/**
 * Binary off-route honesty state surfaced by [DriverMotionModel].
 *
 * Deliberately *not* graded (no "slightly off" / "very off"): the canon panel and Islom resolved
 * the off-route-honesty fork in favour of a binary state plus a visible connector line ([RouteConnector])
 * from the raw GPS to the snapped point. The single edge from [ON_ROUTE] to [OFF_ROUTE] is the one
 * the client consumes to refetch a fresh route — the SDK owns the *signal*, the client owns the
 * refetch (see ADR 0002).
 */
public enum class RouteState {
    /** The latest fix is within the snap threshold; the car follows the route's arc-length progress. */
    ON_ROUTE,

    /** The latest fix is beyond the off-route threshold; the car falls back to chord interpolation. */
    OFF_ROUTE
}

/**
 * The honesty line from a raw GPS fix to where the car is drawn on the route.
 *
 * While [RouteState.ON_ROUTE] but the raw fix sits off the line (within the snap threshold), the
 * renderer draws this short connector so the snap is never a silent lie. Hidden when the raw fix is
 * essentially on the line (see [uz.yalla.maps.config.RouteFollowingConfig.connectorHideThreshold]).
 *
 * @property rawPoint the unsnapped GPS fix.
 * @property snappedPoint the point on the route the car is drawn at.
 */
public data class RouteConnector(
    val rawPoint: uz.yalla.core.geo.GeoPoint,
    val snappedPoint: uz.yalla.core.geo.GeoPoint
)
