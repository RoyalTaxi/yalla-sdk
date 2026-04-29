package uz.yalla.core.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uz.yalla.core.identity.ExecutorId

/**
 * Lightweight executor location data for real-time map tracking.
 *
 * Unlike [Order.Executor], this contains only position data needed
 * for rendering the driver marker on the map.
 *
 * @property heading Vehicle heading in degrees (0-360)
 * @property distance Distance to pickup point in meters
 */
@Serializable
data class Executor(
    @SerialName("id") val id: ExecutorId,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("heading") val heading: Double,
    @SerialName("distance") val distance: Double
)
