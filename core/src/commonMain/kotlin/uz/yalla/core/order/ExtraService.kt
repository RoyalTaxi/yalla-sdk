package uz.yalla.core.order

/**
 * Optional extra service that can be added to an order.
 *
 * Cost can be either a fixed amount or a percentage of the base fare,
 * determined by [costType].
 *
 * @property id Service identifier
 * @property cost Cost value (interpreted based on [costType])
 * @property name Display name
 * @property costType Either [COST_TYPE_COST] for fixed or [COST_TYPE_PERCENT] for percentage
 * @since 0.0.1
 */
data class ExtraService(
    val id: Int,
    val cost: Int,
    val name: String,
    val costType: String
) {
    companion object {
        const val COST_TYPE_COST = "cost"
        const val COST_TYPE_PERCENT = "percent"
    }

    /** Returns true if this service's cost is a percentage of the base fare. */
    val isPercentCost: Boolean
        get() = costType.equals(COST_TYPE_PERCENT, ignoreCase = true)
}
