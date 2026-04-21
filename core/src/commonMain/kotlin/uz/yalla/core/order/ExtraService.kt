package uz.yalla.core.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Optional extra service that can be added to an order.
 *
 * Cost can be either a fixed amount or a percentage of the base fare,
 * determined by [costType]. Use [isPercentCost] to check the cost type
 * without string comparison.
 *
 * ## Usage
 * ```kotlin
 * val service = ExtraService(id = 1, cost = 5000, name = "Child seat", costType = "cost")
 * if (service.isPercentCost) {
 *     val surcharge = basePrice * service.cost / 100
 * } else {
 *     val surcharge = service.cost
 * }
 * ```
 *
 * @property id Service identifier
 * @property cost Cost value (interpreted based on [costType]): fixed amount in smallest
 *   currency unit, or percentage (0-100) of the base fare
 * @property name Display name shown to the user
 * @property costType Either [COST_TYPE_COST] for fixed amount or [COST_TYPE_PERCENT] for percentage
 * @see Order.Taxi.services
 * @since 0.0.1
 */
@Serializable
data class ExtraService(
    @SerialName("id") val id: Int,
    @SerialName("cost") val cost: Int,
    @SerialName("name") val name: String,
    @SerialName("costType") val costType: String
) {
    companion object {
        /** Cost type indicating a fixed monetary amount. */
        const val COST_TYPE_COST = "cost"

        /** Cost type indicating a percentage of the base fare. */
        const val COST_TYPE_PERCENT = "percent"
    }

    /**
     * Returns `true` if this service's cost is a percentage of the base fare.
     *
     * Performs case-insensitive comparison against [COST_TYPE_PERCENT].
     */
    val isPercentCost: Boolean
        get() = costType.equals(COST_TYPE_PERCENT, ignoreCase = true)
}
