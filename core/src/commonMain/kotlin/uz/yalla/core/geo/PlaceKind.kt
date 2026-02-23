package uz.yalla.core.geo

import kotlinx.serialization.Serializable

/**
 * Type of a saved place (home, work, or other).
 */
@Serializable
enum class PlaceKind(
    val id: String,
) {
    Home("home"),
    Work("work"),
    Other("other"),
    ;

    companion object {
        fun from(id: String?): PlaceKind = entries.find { it.id == id?.lowercase() } ?: Other
    }
}
