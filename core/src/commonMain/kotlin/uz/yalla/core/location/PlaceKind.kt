package uz.yalla.core.location

import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/**
 * Type of saved place (home, work, or other).
 */
@Serializable
enum class PlaceKind(val id: String) {
    Home("home"),
    Work("work"),
    Other("other"),
    ;

    companion object {
        fun from(id: String?): PlaceKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Other
        }
    }
}
