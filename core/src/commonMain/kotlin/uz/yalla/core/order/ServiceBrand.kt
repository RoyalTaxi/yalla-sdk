package uz.yalla.core.order

/**
 * Taxi service brand/company information.
 *
 * @property id Unique brand identifier
 * @property name Display name of the service
 * @property photo URL of the brand logo
 * @since 0.0.1
 */
data class ServiceBrand(
    val id: Int,
    val name: String,
    val photo: String
)
