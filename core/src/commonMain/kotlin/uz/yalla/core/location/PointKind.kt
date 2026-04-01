package uz.yalla.core.location

/**
 * Classification of a waypoint in a route.
 *
 * Determines the role of a [PointRequest] in a multi-stop route calculation.
 *
 * @property wireValue API wire-format identifier
 * @see PointRequest
 * @since 0.0.1
 */
enum class PointKind(val wireValue: String) {
    /** Pickup point / route origin. */
    START("start"),

    /** Intermediate waypoint (e.g., an extra stop). */
    POINT("point"),

    /** Final destination / route end. */
    STOP("stop")
}
