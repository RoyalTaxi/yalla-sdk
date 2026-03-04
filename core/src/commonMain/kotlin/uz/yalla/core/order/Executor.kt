package uz.yalla.core.order

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
data class Executor(
    val id: Int,
    val lat: Double,
    val lng: Double,
    val heading: Double,
    val distance: Double
)
