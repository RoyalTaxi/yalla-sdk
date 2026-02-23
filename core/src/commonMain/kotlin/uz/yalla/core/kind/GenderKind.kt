package uz.yalla.core.kind

import kotlinx.serialization.Serializable

/** Gender options for user profiles. */
@Serializable
enum class GenderKind(
    val id: String
) {
    Male("male"),
    Female("female"),
    NotSelected("not_selected");

    companion object {
        fun from(id: String?): GenderKind = entries.find { it.id == id } ?: NotSelected
    }
}
