package uz.yalla.core.location

enum class PointKind(
    val id: String
) {
    START("start"),

    POINT("point"),

    STOP("stop");

    companion object {
        fun from(id: String?): PointKind = entries.find { it.id == id } ?: POINT
    }
}
