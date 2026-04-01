package uz.yalla.core.location

/**
 * Address search result or autocomplete suggestion.
 *
 * Returned by address search APIs, includes distance from current location
 * for sorting by relevance. Displayed in the search results list with
 * [title] as the primary line and [address] as the secondary line.
 *
 * @property id Unique identifier from search provider
 * @property title Primary address text (street, building)
 * @property address Secondary address text (city, region)
 * @property distance Distance from current location in meters (used for sorting)
 * @property lat Latitude in degrees
 * @property lng Longitude in degrees
 * @property isFromDatabase `true` if from local search history rather than the search API
 * @see Address
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
