package uz.yalla.core.location

/**
 * Basic address representation with geographic coordinates.
 *
 * @property id Database identifier, null for addresses not yet persisted
 * @property name Human-readable address string
 * @property lat Latitude
 * @property lng Longitude
 * @property isFromDatabase True if this address was loaded from local storage
 * @since 0.0.1
 */
data class Address(
    val id: Int?,
    val name: String,
    val lat: Double,
    val lng: Double,
    val isFromDatabase: Boolean
)
