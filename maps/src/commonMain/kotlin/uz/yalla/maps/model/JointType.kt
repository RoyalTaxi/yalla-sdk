package uz.yalla.maps.model

/**
 * Vertex joint style for [Polyline][uz.yalla.maps.compose.Polyline] segments.
 *
 * Controls how consecutive line segments are joined at their shared vertex.
 *
 * @since 0.0.1
 */
enum class JointType {
    /** Platform default joint style. */
    Default,

    /** Beveled (flat, clipped) join at the vertex. */
    Bevel,

    /** Rounded join at the vertex. */
    Round,
}
