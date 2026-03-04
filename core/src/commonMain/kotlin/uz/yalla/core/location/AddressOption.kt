package uz.yalla.core.location

/**
 * Address search result or autocomplete suggestion.
 *
 * Returned by address search APIs, includes distance from current location
 * for sorting relevance.
 *
 * @property id Unique identifier from search provider
 * @property title Primary address text (street, building)
 * @property address Secondary address text (city, region)
 * @property distance Distance from current location in meters
 * @property lat Latitude
 * @property lng Longitude
 * @property isFromDatabase True if from local history rather than search API
 * @since 0.0.1
 */
data class AddressOption(
    val id: Int,
    val title: String,
    val address: String,
    val distance: Double,
    val lat: Double,
    val lng: Double,
    val isFromDatabase: Boolean
)
