package uz.yalla.core.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.yalla.core.identity.ExtraServiceId

/**
 * Optional extra service that can be added to an order.
 *
 * Cost can be either a fixed amount or a percentage of the base fare,
 * determined by [costType].
 *
 * ## Usage
 * ```kotlin
 * val service = ExtraService(id = 1, cost = 5000, name = "Child seat", costType = ExtraService.CostType.Fixed)
 * val surcharge = when (service.costType) {
 *     ExtraService.CostType.Fixed   -> service.cost
 *     ExtraService.CostType.Percent -> basePrice * service.cost / 100
 * }
 * ```
 *
 * @property cost Cost value (interpreted based on [costType]): fixed amount in
 *   smallest currency unit, or percentage (0-100) of the base fare
 * @see Order.Taxi.services
 */
@Serializable
data class ExtraService(
    @SerialName("id") val id: ExtraServiceId,
    @SerialName("cost") val cost: Int,
    @SerialName("name") val name: String,
    @SerialName("costType") val costType: CostType
) {
    @Serializable
    enum class CostType {
        @SerialName("cost")
        Fixed,

        @SerialName("percent")
        Percent
    }
}
