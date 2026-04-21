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
 * @property id Wire-format identifier used in API communication
 * @see SavedAddress
 * @since 0.0.1
 */
@Serializable
enum class PlaceKind(val id: String) {
    /** User's home address. */
    @SerialName("home")
    Home("home"),

    /** User's work/office address. */
    @SerialName("work")
    Work("work"),

    /** Any other saved location. */
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
         * @param id Wire-format identifier from the API, or `null`
         * @return The matching [PlaceKind], defaulting to [Other]
         */
        fun from(id: String?): PlaceKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: Other
        }
    }
}
