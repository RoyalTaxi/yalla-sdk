package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Classification of a waypoint in a route.
 *
 * Determines the role of a [PointRequest] in a multi-stop route calculation.
 *
 * @property wireValue API wire-format identifier
 * @see PointRequest
 * @since 0.0.1
 */
@Serializable
enum class PointKind(val wireValue: String) {
    /** Pickup point / route origin. */
    @SerialName("start")
    START("start"),

    /** Intermediate waypoint (e.g., an extra stop). */
    @SerialName("point")
    POINT("point"),

    /** Final destination / route end. */
    @SerialName("stop")
    STOP("stop")
}
