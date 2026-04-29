package uz.yalla.core.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/**
 * Category of a user's saved place.
 *
 * Each saved address is classified into one of these categories,
 * which determines its icon and sort order in the UI.
 *
 * @see SavedAddress
 */
@Serializable
enum class PlaceKind(val id: String) {
    @SerialName("home")
    Home("home"),

    @SerialName("work")
    Work("work"),

    @SerialName("other")
    Other("other"),
    ;

    companion object {
        /**
         * Parses an API string into the corresponding [PlaceKind].
         *
         * Performs case-insensitive matching after trimming whitespace.
         * Returns [Other] for `null` or unrecognized values.
         *
         * @return The matching [PlaceKind], defaulting to [Other]
         */
        fun from(id: String?): PlaceKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Other
        }
    }
}
