package uz.yalla.core.location

/**
 * Classification of a waypoint in a route.
 *
 * @property wireValue API wire-format identifier
 * @since 0.0.1
 */
enum class PointKind(val wireValue: String) {
    START("start"),
    POINT("point"),
    STOP("stop")
}
