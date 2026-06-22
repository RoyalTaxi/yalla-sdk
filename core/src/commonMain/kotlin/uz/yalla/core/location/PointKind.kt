package uz.yalla.core.location

import uz.yalla.core.util.normalizedId

public enum class PointKind(
    public val id: String
) {
    Start("start"),

    Point("point"),

    Stop("stop");

    public companion object {
        public fun from(id: String?): PointKind = entries.find { it.id == id.normalizedId() } ?: Point
    }
}
