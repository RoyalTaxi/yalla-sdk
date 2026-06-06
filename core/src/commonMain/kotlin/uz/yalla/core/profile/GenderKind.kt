package uz.yalla.core.profile

import uz.yalla.core.util.normalizedId

enum class GenderKind(
    val id: String
) {
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
