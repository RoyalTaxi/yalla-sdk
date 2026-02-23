package uz.yalla.core.model

data class ServiceModel(
    val cost: Int,
    val costType: String = COST_TYPE_COST,
    val id: Int,
    val name: String
) {
    companion object {
        const val COST_TYPE_COST = "cost"
        const val COST_TYPE_PERCENT = "percent"
    }

    val isPercentCost: Boolean
        get() = costType.equals(COST_TYPE_PERCENT, ignoreCase = true)
}
