package uz.yalla.core.order

import uz.yalla.core.identity.ExtraServiceId
import uz.yalla.core.util.normalizedId

data class ExtraService(
    val id: ExtraServiceId,
    val cost: Int,
    val name: String,
    val costType: CostType
) {
    enum class CostType(val id: String) {
        Fixed("cost"),
        Percent("percent");

        companion object {
            fun from(id: String?): CostType = entries.find { it.id == id.normalizedId() } ?: Fixed
        }
    }
}
