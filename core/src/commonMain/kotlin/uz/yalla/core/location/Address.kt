package uz.yalla.core.location

/**
 * Basic address representation with geographic coordinates.
 *
 * Used as the standard address model throughout the order creation flow,
 * representing both pickup and destination points.
 *
 * @property id Database identifier, `null` for addresses not yet persisted
 * @property name Human-readable address string
 * @property lat Latitude in degrees
 * @property lng Longitude in degrees
 * @property isFromDatabase `true` if this address was loaded from local history
 * @see AddressOption
 * @see SavedAddress
 * @since 0.0.1
 */
data class Address(
    val id: Int?,
    val name: String,
    val lat: Double,
    val lng: Double,
    val isFromDatabase: Boolean
)
