package uz.yalla.core.location

import uz.yalla.core.util.normalizedId

/** A waypoint's role within a route: the [Start], an intermediate [Point], or the [Stop]. */
public enum class PointKind(
    public val id: String
) {
    Start("start"),

    Point("point"),

    Stop("stop");

    public companion object {
        /**
         * Decodes a wire id into a [PointKind], normalizing case/whitespace and defaulting to
         * [Point] on unknown/null — matching every sibling `from` decoder in the SDK.
         */
        public fun from(id: String?): PointKind = entries.find { it.id == id.normalizedId() } ?: Point
    }
}
