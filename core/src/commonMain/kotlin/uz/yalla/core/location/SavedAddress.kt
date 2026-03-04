package uz.yalla.core.location

/**
 * User's saved/favorite address with navigation metrics.
 *
 * @property distance Distance from current location in meters
 * @property duration Estimated travel time in seconds
 * @property lat Latitude
 * @property lng Longitude
 * @property address Full address string
 * @property title User-assigned label
 * @property kind Place category (home, work, other)
 * @property parent Parent location info (e.g., city name)
 * @since 0.0.1
 */
data class SavedAddress(
    val distance: Double,
    val duration: Double,
    val lat: Double,
    val lng: Double,
    val address: String,
    val title: String,
    val kind: PlaceKind,
    val parent: Parent
) {
    /** Parent location for hierarchical address display. */
    data class Parent(val name: String?)
}
