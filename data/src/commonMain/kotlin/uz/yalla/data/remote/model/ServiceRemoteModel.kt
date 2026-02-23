package uz.yalla.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceRemoteModel(
    val cost: Int?,
    @SerialName("cost_type")
    val costType: String? = "cost",
    val id: Int?,
    val name: String?
) {
    companion object {
        const val COST_TYPE_COST = "cost"
        const val COST_TYPE_PERCENT = "percent"

        fun isPercentType(type: String?): Boolean =
            type?.equals(COST_TYPE_PERCENT, ignoreCase = true) ?: false
    }
}
