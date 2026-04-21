package uz.yalla.core.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Lightweight executor location data for real-time map tracking.
 *
 * Unlike [Order.Executor], this contains only position data needed
 * for rendering the driver marker on the map.
 *
 * @property id Driver identifier
 * @property lat Current latitude
 * @property lng Current longitude
 * @property heading Vehicle heading in degrees (0-360)
 * @property distance Distance to pickup point in meters
 * @since 0.0.1
 */
@Serializable
data class Executor(
    @SerialName("id") val id: Int,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("heading") val heading: Double,
    @SerialName("distance") val distance: Double
)
