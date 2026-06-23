package uz.yalla.maps.config

/**
 * Tunable thresholds for route-following motion, all in commonMain so a distance means the same
 * thing on Android and iOS.
 *
 * This replaces the single `30m` literal the ported engine used for three different jobs (snap,
 * arrival, connector visibility) with named distances, plus the hysteresis and cooldown the canon
 * panel required to keep off-route from chattering or storming refetches:
 *
 * - **Three distances.** [routeSnapThreshold] decides whether a raw GPS fix is close enough to
 *   follow the route at all; [routeArrivalThreshold] decides when the car has reached the route
 *   end; [connectorHideThreshold] hides the raw-to-snapped connector line once the fix sits on the
 *   line (no clutter when honesty is unnecessary).
 * - **Enter/exit hysteresis.** A single threshold flickers ON_ROUTE/OFF_ROUTE when a fix hovers at
 *   the boundary. The state flips to OFF_ROUTE only past [offRouteEnterMeters] and back to ON_ROUTE
 *   only within the tighter [offRouteExitMeters]. The band between them is sticky.
 * - **Refetch cooldown.** [refetchCooldownMillis] suppresses repeat off-route signals so a client
 *   that refetches on the signal cannot be driven into a refetch storm by GPS noise near the edge.
 * - **Forward window.** [backWindowMeters] / [forwardWindowMeters] bound the arc-length search so
 *   projection stays on the current leg (no un-eating across loops / parallel streets) and costs
 *   O(window) per frame instead of O(route).
 *
 * @property routeSnapThreshold max cross-track meters for a fix to be followed along the route.
 * @property routeArrivalThreshold meters from the route end at which the car counts as arrived.
 * @property connectorHideThreshold cross-track meters below which the connector line is hidden.
 * @property offRouteEnterMeters cross-track meters beyond which the state enters OFF_ROUTE.
 * @property offRouteExitMeters cross-track meters within which the state returns to ON_ROUTE.
 * @property refetchCooldownMillis min interval between edge-latched off-route signals.
 * @property backWindowMeters how far behind last progress a legitimate reverse may project.
 * @property forwardWindowMeters how far ahead of last progress to search for forward motion.
 * @property maxHeadingTurnRatePerSecond cap on displayed heading turn rate (degrees/second).
 */
public data class RouteFollowingConfig(
    val routeSnapThreshold: Double = 30.0,
    val routeArrivalThreshold: Double = 25.0,
    val connectorHideThreshold: Double = 4.0,
    val offRouteEnterMeters: Double = 30.0,
    val offRouteExitMeters: Double = 15.0,
    val refetchCooldownMillis: Long = 15_000L,
    val backWindowMeters: Double = 5.0,
    val forwardWindowMeters: Double = 50.0,
    val maxHeadingTurnRatePerSecond: Double = 120.0
)
