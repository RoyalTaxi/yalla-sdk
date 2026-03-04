package uz.yalla.core.profile

import kotlinx.serialization.Serializable
import uz.yalla.core.util.normalizedId

/** Gender options for user profiles. */
@Serializable
enum class GenderKind(val id: String) {
    Male("male"),
    Female("female"),
    NotSelected("not_selected");

    companion object {
        fun from(id: String?): GenderKind {
            val normalizedId = id.normalizedId()
            return entries.find { it.id == normalizedId } ?: NotSelected
        }
    }
}
