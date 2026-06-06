package uz.yalla.core.location

import uz.yalla.core.util.normalizedId

enum class PlaceKind(
    val id: String
) {
    Home("home"),

    Work("work"),

    Other("other")
    ;

    companion object {
        fun from(id: String?): PlaceKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Other
        }
    }
}
