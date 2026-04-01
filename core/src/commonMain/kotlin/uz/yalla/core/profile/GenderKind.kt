package uz.yalla.core.profile

import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/**
 * Gender options for user profiles.
 *
 * Used in the profile editing screen and sent to the backend
 * during profile updates.
 *
 * @property id Wire-format identifier used in API communication
 * @see Client
 * @since 0.0.1
 */
@Serializable
enum class GenderKind(val id: String) {
    /** Male gender. */
    Male("male"),

    /** Female gender. */
    Female("female"),

    /** User has not selected a gender. Default value. */
    NotSelected("not_selected");

    companion object {
        /**
         * Parses an API string into the corresponding [GenderKind].
         *
         * Performs case-insensitive matching after trimming whitespace.
         * Returns [NotSelected] for `null` or unrecognized values.
         *
         * @param id Wire-format identifier from the API, or `null`
         * @return The matching [GenderKind], defaulting to [NotSelected]
         */
        fun from(id: String?): GenderKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: NotSelected
        }
    }
}
