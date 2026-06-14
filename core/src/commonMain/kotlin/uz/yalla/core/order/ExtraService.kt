package uz.yalla.core.order

import uz.yalla.core.identity.ExtraServiceId
import uz.yalla.core.util.normalizedId

public data class ExtraService(
    val id: ExtraServiceId,
    val cost: Int,
    val name: String,
    val costType: CostType,
    val icon: String? = null
) {
    public enum class CostType(
        public val id: String
    ) {
        Fixed("cost"),
        Percent("percent");

        public companion object {
            public fun from(id: String?): CostType = entries.find { it.id == id.normalizedId() } ?: Fixed
        }
    }
}
