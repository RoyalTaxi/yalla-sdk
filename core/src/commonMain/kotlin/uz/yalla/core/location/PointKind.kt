package uz.yalla.core.location

public enum class PointKind(
    public val id: String
) {
    START("start"),

    POINT("point"),

    STOP("stop");

    public companion object {
        public fun from(id: String?): PointKind = entries.find { it.id == id } ?: POINT
    }
}
