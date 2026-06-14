package uz.yalla.core.location

import uz.yalla.core.util.normalizedId

public enum class PlaceKind(
    public val id: String
) {
    Home("home"),

    Work("work"),

    Other("other")
    ;

    public companion object {
        public fun from(id: String?): PlaceKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Other
        }
    }
}
